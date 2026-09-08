package com.seanconroy.fiae;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ProgressResourceTest {

    @Test
    void validAuthenticatedUserCanRecordProgress() {
        String apiKey = createApiKey();

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", true))
                .when()
                .post("/api/progress/card-auth-answer/answer")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("cardSlug", equalTo("card-auth-answer"))
                .body("timesSeen", equalTo(1))
                .body("timesCorrect", equalTo(1))
                .body("lastSeenAt", notNullValue());
    }

    @Test
    void firstAnswerCreatesProgress() {
        String apiKey = createApiKey();

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", false))
                .when()
                .post("/api/progress/card-first-answer/answer")
                .then()
                .statusCode(200);

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/progress")
                .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("data[0].cardSlug", equalTo("card-first-answer"))
                .body("data[0].timesSeen", equalTo(1))
                .body("data[0].timesCorrect", equalTo(0));
    }

    @Test
    void subsequentAnswerUpdatesTheSameUserCardRow() {
        String apiKey = createApiKey();

        JsonPath firstAnswer = answer(apiKey, "card-update-row", true);
        JsonPath secondAnswer = answer(apiKey, "card-update-row", false);

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/progress")
                .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("data[0].id", equalTo((int) firstAnswer.getLong("id")))
                .body("data[0].id", equalTo((int) secondAnswer.getLong("id")))
                .body("data[0].timesSeen", equalTo(2))
                .body("data[0].timesCorrect", equalTo(1));
    }

    @Test
    void correctAnswerIncrementsTimesCorrect() {
        String apiKey = createApiKey();

        answer(apiKey, "card-correct-count", true);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", true))
                .when()
                .post("/api/progress/card-correct-count/answer")
                .then()
                .statusCode(200)
                .body("timesSeen", equalTo(2))
                .body("timesCorrect", equalTo(2));
    }

    @Test
    void incorrectAnswerIncrementsTimesSeenButNotTimesCorrect() {
        String apiKey = createApiKey();

        answer(apiKey, "card-incorrect-count", true);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", false))
                .when()
                .post("/api/progress/card-incorrect-count/answer")
                .then()
                .statusCode(200)
                .body("timesSeen", equalTo(2))
                .body("timesCorrect", equalTo(1));
    }

    @Test
    void authenticatedUserCanReadTheirOwnProgress() {
        String apiKey = createApiKey();

        answer(apiKey, "card-read-own-progress", true);

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/progress")
                .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("data[0].cardSlug", equalTo("card-read-own-progress"))
                .body("data[0].timesSeen", equalTo(1))
                .body("data[0].timesCorrect", equalTo(1));
    }

    @Test
    void anotherUserCannotSeeThatProgress() {
        String firstUserApiKey = createApiKey();
        String secondUserApiKey = createApiKey();

        answer(firstUserApiKey, "card-user-isolation", true);

        given()
                .header("X-API-Key", secondUserApiKey)
                .when()
                .get("/api/progress")
                .then()
                .statusCode(200)
                .body("data.size()", equalTo(0));
    }

    @Test
    void missingOrInvalidApiKeyCannotWriteProgress() {
        given()
                .contentType("application/json")
                .body(Map.of("correct", true))
                .when()
                .post("/api/progress/card-no-auth/answer")
                .then()
                .statusCode(403);

        given()
                .contentType("application/json")
                .header("X-API-Key", "invalid-api-key")
                .body(Map.of("correct", true))
                .when()
                .post("/api/progress/card-invalid-auth/answer")
                .then()
                .statusCode(403);
    }

    @Test
    void getProgressRejectsMissingOrInvalidApiKey() {
        given()
                .when()
                .get("/api/progress")
                .then()
                .statusCode(403);

        given()
                .header("X-API-Key", "invalid-api-key")
                .when()
                .get("/api/progress")
                .then()
                .statusCode(403);
    }

    @Test
    void blankOrWhitespaceCardSlugIsRejected() {
        String apiKey = createApiKey();

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", true))
                .when()
                .post("/api/progress/%20%20/answer")
                .then()
                .statusCode(400)
                .body("message", equalTo("Path parameter 'cardSlug' cannot be blank"))
                .body("status", equalTo(400));
    }

    @Test
    void missingOrNullCorrectIsRejected() {
        String apiKey = createApiKey();

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body("{}")
                .when()
                .post("/api/progress/card-missing-correct/answer")
                .then()
                .statusCode(400)
                .body("message", equalTo("must not be null"))
                .body("status", equalTo(400));

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body("{\"correct\":null}")
                .when()
                .post("/api/progress/card-null-correct/answer")
                .then()
                .statusCode(400)
                .body("message", equalTo("must not be null"))
                .body("status", equalTo(400));
    }

    @Test
    void publicCardEndpointsRemainAccessibleWithoutAuthentication() {
        given()
                .when()
                .get("/api/cards/all")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    private String createApiKey() {
        String email = "test-" + UUID.randomUUID() + "@example.com";

        return given()
                .contentType("application/json")
                .header("X-Admin-Token", "dev-admin-token")
                .body(Map.of("email", email))
                .when()
                .post("/api/admin/whitelist")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("apiKey");
    }

    private JsonPath answer(String apiKey, String cardSlug, boolean correct) {
        return given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", correct))
                .when()
                .post("/api/progress/" + cardSlug + "/answer")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
    }
}

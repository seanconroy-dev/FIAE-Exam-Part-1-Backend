package com.seanconroy.fiae;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class QuizStateResourceTest {

    @Test
    void missingApiKeyCannotGetQuizState() {
        given()
                .when()
                .get("/api/quiz-state/all")
                .then()
                .statusCode(403);
    }

    @Test
    void invalidApiKeyCannotGetQuizState() {
        given()
                .header("X-API-Key", "invalid-key")
                .when()
                .get("/api/quiz-state/all")
                .then()
                .statusCode(403);
    }

    @Test
    void authenticatedUserWithNoStateReceives404() {
        String apiKey = createApiKey();

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/quiz-state/" + uniqueModuleKey())
                .then()
                .statusCode(404);
    }

    @Test
    void authenticatedUserCanCreateAndReadState() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();
        Map<String, Object> snapshot = validSnapshot(null);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201)
                .body("moduleKey", equalTo(moduleKey))
                .body("moduleName", equalTo(null))
                .body("queueSlugs[0]", equalTo("card-slug-a"))
                .body("queueSlugs[1]", equalTo("card-slug-b"))
                .body("queueSlugs[2]", equalTo("card-slug-c"))
                .body("currentIndex", equalTo(1))
                .body("resultsBySlug.card-slug-a", equalTo("correct"))
                .body("completed", equalTo(false))
                .body("revision", equalTo(4))
                .body("startedAt", equalTo(snapshot.get("startedAt")))
                .body("updatedAt", notNullValue());

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(200)
                .body("moduleKey", equalTo(moduleKey))
                .body("queueSlugs[0]", equalTo("card-slug-a"))
                .body("resultsBySlug.card-slug-a", equalTo("correct"));
    }

    @Test
    void queueOrderIsPreservedExactly() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Hardware");
        snapshot.put("queueSlugs", List.of("first", "third", "second"));
        snapshot.put("currentIndex", 2);
        snapshot.put("resultsBySlug", Map.of("first", "correct"));

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201);

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(200)
                .body("queueSlugs[0]", equalTo("first"))
                .body("queueSlugs[1]", equalTo("third"))
                .body("queueSlugs[2]", equalTo("second"));
    }

    @Test
    void resultsBySlugIsPreservedExactly() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Networking");
        snapshot.put("queueSlugs", List.of("a", "b", "c"));
        snapshot.put("currentIndex", 2);
        Map<String, String> results = new LinkedHashMap<>();
        results.put("a", "wrong");
        results.put("b", "correct");
        snapshot.put("resultsBySlug", results);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201);

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(200)
                .body("resultsBySlug.a", equalTo("wrong"))
                .body("resultsBySlug.b", equalTo("correct"));
    }

    @Test
    void newerRevisionReplacesOlderSnapshot() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> first = validSnapshot("Old Name");
        first.put("revision", 1);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(first)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201);

        Map<String, Object> second = validSnapshot("New Name");
        second.put("revision", 2);
        second.put("currentIndex", 2);
        second.put("resultsBySlug", Map.of("card-slug-a", "correct", "card-slug-b", "wrong"));

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(second)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(200)
                .body("moduleName", equalTo("New Name"))
                .body("currentIndex", equalTo(2))
                .body("revision", equalTo(2))
                .body("resultsBySlug.card-slug-b", equalTo("wrong"));
    }

    @Test
    void identicalSameRevisionPutIsIdempotent() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Module X");
        snapshot.put("revision", 7);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201)
                .extract();

        String storedUpdatedAt = given()
                .header("X-API-Key", apiKey)
                .when()
                .get("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("updatedAt");

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(200)
                .body("revision", equalTo(7))
                .body("updatedAt", equalTo(storedUpdatedAt));
    }

    @Test
    void differentSameRevisionPutReturns409() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> first = validSnapshot("Module X");
        first.put("revision", 4);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(first)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201);

        Map<String, Object> different = validSnapshot("Module X");
        different.put("revision", 4);
        different.put("currentIndex", 2);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(different)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("currentState.revision", equalTo(4));
    }

    @Test
    void olderRevisionReturns409() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> newer = validSnapshot("Module X");
        newer.put("revision", 5);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(newer)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201);

        Map<String, Object> older = validSnapshot("Module X");
        older.put("revision", 4);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(older)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(409)
                .body("currentState.revision", equalTo(5));
    }

    @Test
    void oneUserCannotReadAnotherUsersState() {
        String firstApiKey = createApiKey();
        String secondApiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        given()
                .contentType("application/json")
                .header("X-API-Key", firstApiKey)
                .body(validSnapshot("Shared module"))
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(201);

        given()
                .header("X-API-Key", secondApiKey)
                .when()
                .get("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(404);
    }

    @Test
    void twoModulesForSameUserRemainIndependent() {
        String apiKey = createApiKey();
        String moduleA = uniqueModuleKey();
        String moduleB = uniqueModuleKey();

        Map<String, Object> a = validSnapshot("Module A");
        a.put("revision", 1);

        Map<String, Object> b = validSnapshot("Module B");
        b.put("revision", 9);
        b.put("currentIndex", 0);

        given().contentType("application/json").header("X-API-Key", apiKey).body(a)
                .when().put("/api/quiz-state/" + moduleA).then().statusCode(201);

        given().contentType("application/json").header("X-API-Key", apiKey).body(b)
                .when().put("/api/quiz-state/" + moduleB).then().statusCode(201);

        given().header("X-API-Key", apiKey)
                .when().get("/api/quiz-state/" + moduleA)
                .then().statusCode(200)
                .body("moduleName", equalTo("Module A"))
                .body("revision", equalTo(1));

        given().header("X-API-Key", apiKey)
                .when().get("/api/quiz-state/" + moduleB)
                .then().statusCode(200)
                .body("moduleName", equalTo("Module B"))
                .body("revision", equalTo(9));
    }

    @Test
    void deletingOneModuleDoesNotDeleteAnother() {
        String apiKey = createApiKey();
        String moduleA = uniqueModuleKey();
        String moduleB = uniqueModuleKey();

        given().contentType("application/json").header("X-API-Key", apiKey).body(validSnapshot("Module A"))
                .when().put("/api/quiz-state/" + moduleA).then().statusCode(201);

        given().contentType("application/json").header("X-API-Key", apiKey).body(validSnapshot("Module B"))
                .when().put("/api/quiz-state/" + moduleB).then().statusCode(201);

        given().header("X-API-Key", apiKey)
                .when().delete("/api/quiz-state/" + moduleA)
                .then().statusCode(204);

        given().header("X-API-Key", apiKey)
                .when().get("/api/quiz-state/" + moduleA)
                .then().statusCode(404);

        given().header("X-API-Key", apiKey)
                .when().get("/api/quiz-state/" + moduleB)
                .then().statusCode(200);
    }

    @Test
    void deleteMissingStateIsIdempotent() {
        String apiKey = createApiKey();

        given()
                .header("X-API-Key", apiKey)
                .when()
                .delete("/api/quiz-state/" + uniqueModuleKey())
                .then()
                .statusCode(204);
    }

    @Test
    void deleteQuizStateDoesNotDeleteLearningProgress() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(Map.of("correct", true))
                .when()
                .post("/api/progress/card-keep-progress/answer")
                .then()
                .statusCode(200);

        given().contentType("application/json").header("X-API-Key", apiKey).body(validSnapshot("Module X"))
                .when().put("/api/quiz-state/" + moduleKey).then().statusCode(201);

        given().header("X-API-Key", apiKey)
                .when().delete("/api/quiz-state/" + moduleKey)
                .then().statusCode(204);

        given().header("X-API-Key", apiKey)
                .when().get("/api/progress")
                .then().statusCode(200)
                .body("data.size()", equalTo(1))
                .body("data[0].cardSlug", equalTo("card-keep-progress"));
    }

    @Test
    void invalidResultValueIsRejected() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Module X");
        snapshot.put("resultsBySlug", Map.of("card-slug-a", "maybe"));

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(400)
                .body("message", containsString("resultsBySlug values must be exactly 'correct' or 'wrong'"));
    }

    @Test
    void resultSlugNotInQueueIsRejected() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Module X");
        snapshot.put("resultsBySlug", Map.of("outside-slug", "correct"));

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(400)
                .body("message", containsString("resultsBySlug contains slug that is not present in queueSlugs"));
    }

    @Test
    void duplicateQueueSlugIsRejected() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Module X");
        snapshot.put("queueSlugs", List.of("dup", "dup"));
        snapshot.put("currentIndex", 1);
        snapshot.put("resultsBySlug", Map.of("dup", "correct"));

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(400)
                .body("message", containsString("queueSlugs must not contain duplicate values"));
    }

    @Test
    void invalidCurrentIndexIsRejected() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Module X");
        snapshot.put("currentIndex", 3);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(400)
                .body("message", containsString("when completed is false"));
    }

    @Test
    void completedAndCurrentIndexInconsistencyIsRejected() {
        String apiKey = createApiKey();
        String moduleKey = uniqueModuleKey();

        Map<String, Object> snapshot = validSnapshot("Module X");
        snapshot.put("completed", true);
        snapshot.put("currentIndex", 1);

        given()
                .contentType("application/json")
                .header("X-API-Key", apiKey)
                .body(snapshot)
                .when()
                .put("/api/quiz-state/" + moduleKey)
                .then()
                .statusCode(400)
                .body("message", containsString("completed quiz must use currentIndex equal to queueSlugs length"));
    }

    @Test
    void healthEndpointWorksWithoutAuthentication() {
        given()
                .when()
                .get("/api/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("ok"));
    }

    @Test
    void corsPreflightAllowsGithubPagesOriginAndApiKeyHeader() {
        given()
                .header("Origin", "https://seanconroy-dev.github.io")
                .header("Access-Control-Request-Method", "PUT")
                .header("Access-Control-Request-Headers", "content-type,x-api-key")
                .when()
                .options("/api/quiz-state/all")
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", equalTo("https://seanconroy-dev.github.io"))
                .header("Access-Control-Allow-Methods", containsString("PUT"))
                .header("Access-Control-Allow-Headers", containsString("x-API-key"));
    }

    private String createApiKey() {
        String email = "quiz-state-" + UUID.randomUUID() + "@example.com";

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

    private String uniqueModuleKey() {
        return "module-" + UUID.randomUUID();
    }

    private Map<String, Object> validSnapshot(String moduleName) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("moduleName", moduleName);

        List<String> queue = new ArrayList<>();
        queue.add("card-slug-a");
        queue.add("card-slug-b");
        queue.add("card-slug-c");
        snapshot.put("queueSlugs", queue);

        snapshot.put("currentIndex", 1);
        snapshot.put("resultsBySlug", new LinkedHashMap<>(Map.of("card-slug-a", "correct")));
        snapshot.put("completed", false);
        snapshot.put("revision", 4);
        snapshot.put("startedAt", Instant.parse("2026-09-16T19:00:00Z").toString());
        return snapshot;
    }
}

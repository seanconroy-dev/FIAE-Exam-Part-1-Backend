package com.seanconroy.fiae;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class GreetingResourceTest {
    @Test
    void testCardsEndpointReturns200() {
        given()
          .when().get("/api/cards/all")
          .then()
             .statusCode(200)
             .body(notNullValue());
    }

}
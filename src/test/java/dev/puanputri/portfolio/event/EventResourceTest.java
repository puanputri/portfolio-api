package dev.puanputri.portfolio.event;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link EventResource}.
 * Uses H2 in-memory DB.
 */
@QuarkusTest
class EventResourceTest {

    @Test
    void postValidPageView_returns204() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "eventType": "PAGE_VIEW",
                          "page": "/projects",
                          "referrer": "https://google.com",
                          "userAgent": "Mozilla/5.0 (test)"
                        }
                        """)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(204);
    }

    @Test
    void postValidProjectClick_returns204() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "eventType": "PROJECT_CLICK",
                          "page": "/projects/portfolio-api",
                          "referrer": null,
                          "userAgent": "Mozilla/5.0 (test)"
                        }
                        """)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(204);
    }

    @Test
    void postEvent_missingEventType_returns422() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "eventType": "",
                          "page": "/home"
                        }
                        """)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(422)
                .contentType("application/problem+json")
                .body("status", equalTo(422))
                .body("title", equalTo("Unprocessable Entity"))
                .body("detail", containsString("eventType"));
    }

    @Test
    void postEvent_missingPage_returns422() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "eventType": "PAGE_VIEW",
                          "page": ""
                        }
                        """)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(422)
                .contentType("application/problem+json")
                .body("status", equalTo(422))
                .body("detail", containsString("page"));
    }

    @Test
    void postEvent_invalidEventType_returns422() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "eventType": "INVALID_TYPE",
                          "page": "/home"
                        }
                        """)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(422)
                .contentType("application/problem+json")
                .body("status", equalTo(422));
    }
}

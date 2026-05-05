package dev.puanputri.portfolio.contact;

import dev.puanputri.portfolio.common.RateLimiterService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link ContactResource}.
 * Uses H2 in-memory DB and Quarkus mock mailer (no real SMTP).
 */
@QuarkusTest
class ContactResourceTest {

    @Inject
    RateLimiterService rateLimiterService;

    @BeforeEach
    void resetRateLimiter() {
        rateLimiterService.resetAll();
    }

    @Test
    void postValidContact_returns201() {
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", "10.0.0.1")
                .body("""
                        {
                          "name": "Puan Putri",
                          "email": "puanputrisaqinahf@gmail.com",
                          "message": "Hello, this is a test message from the portfolio site."
                        }
                        """)
                .when()
                .post("/api/v1/contact")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo("Puan Putri"))
                .body("email", equalTo("puanputrisaqinahf@gmail.com"))
                .body("createdAt", notNullValue());
    }

    @Test
    void postInvalidContact_missingName_returns422() {
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", "10.0.0.2")
                .body("""
                        {
                          "name": "",
                          "email": "puanputrisaqinahf@gmail.com",
                          "message": "Hello, this is a test message from the portfolio site."
                        }
                        """)
                .when()
                .post("/api/v1/contact")
                .then()
                .statusCode(422)
                .contentType("application/problem+json")
                .body("status", equalTo(422))
                .body("title", equalTo("Unprocessable Entity"))
                .body("detail", containsString("name"));
    }

    @Test
    void postInvalidContact_badEmail_returns422() {
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", "10.0.0.3")
                .body("""
                        {
                          "name": "Puan Putri",
                          "email": "not-an-email",
                          "message": "Hello, this is a test message from the portfolio site."
                        }
                        """)
                .when()
                .post("/api/v1/contact")
                .then()
                .statusCode(422)
                .contentType("application/problem+json")
                .body("status", equalTo(422))
                .body("detail", containsString("email"));
    }

    @Test
    void postInvalidContact_messageTooShort_returns422() {
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", "10.0.0.4")
                .body("""
                        {
                          "name": "Puan Putri",
                          "email": "puanputrisaqinahf@gmail.com",
                          "message": "Short"
                        }
                        """)
                .when()
                .post("/api/v1/contact")
                .then()
                .statusCode(422)
                .contentType("application/problem+json")
                .body("status", equalTo(422));
    }

    @Test
    void postContact_sixTimesFromSameIp_sixthReturns429() {
        String testIp = "192.168.99.99";

        // First 5 requests should succeed
        for (int i = 0; i < 5; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .header("X-Forwarded-For", testIp)
                    .body(String.format("""
                            {
                              "name": "Test User %d",
                              "email": "test%d@example.com",
                              "message": "This is test message number %d, long enough to pass validation."
                            }
                            """, i, i, i))
                    .when()
                    .post("/api/v1/contact")
                    .then()
                    .statusCode(201);
        }

        // 6th request must be rate-limited
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", testIp)
                .body("""
                        {
                          "name": "Test User",
                          "email": "test@example.com",
                          "message": "This is the 6th message that should be rate-limited."
                        }
                        """)
                .when()
                .post("/api/v1/contact")
                .then()
                .statusCode(429)
                .contentType("application/problem+json")
                .body("status", equalTo(429))
                .body("title", equalTo("Too Many Requests"));
    }
}

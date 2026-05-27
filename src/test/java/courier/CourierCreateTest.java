package courier;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {

    private String createdCourierLogin;
    private String createdCourierPassword;
    private int createdCourierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void tearDown() {
        if (createdCourierId > 0) {
            given()
                    .header("Content-Type", "application/json")
                    .delete("/api/v1/courier/" + createdCourierId)
                    .then().statusCode(200);
        }
    }

    private int getCourierId(String login, String password) {
        String body = "{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}";
        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then().extract().path("id");
    }

    @Test
    public void createCourierSuccess() {
        createdCourierLogin = "courier_" + System.currentTimeMillis();
        createdCourierPassword = "pass123";
        String body = "{\"login\":\"" + createdCourierLogin + "\",\"password\":\"" + createdCourierPassword + "\",\"firstName\":\"Иван\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));

        createdCourierId = getCourierId(createdCourierLogin, createdCourierPassword);
    }

    @Test
    public void cannotCreateDuplicateCourier() {
        createdCourierLogin = "duplicate_test_" + System.currentTimeMillis();
        createdCourierPassword = "pass123";
        String body = "{\"login\":\"" + createdCourierLogin + "\",\"password\":\"" + createdCourierPassword + "\",\"firstName\":\"Петр\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier");

        createdCourierId = getCourierId(createdCourierLogin, createdCourierPassword);

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    public void createCourierWithoutLoginFails() {
        String body = "{\"password\":\"pass123\",\"firstName\":\"Сергей\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(400);
    }

    @Test
    public void createCourierWithoutPasswordFails() {
        String body = "{\"login\":\"test_login_" + System.currentTimeMillis() + "\",\"firstName\":\"Алексей\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(400);
    }

    @Test
    public void successResponseReturnsOkTrue() {
        createdCourierLogin = "ok_true_test_" + System.currentTimeMillis();
        createdCourierPassword = "pass123";
        String body = "{\"login\":\"" + createdCourierLogin + "\",\"password\":\"" + createdCourierPassword + "\",\"firstName\":\"Дмитрий\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .body("ok", equalTo(true));

        createdCourierId = getCourierId(createdCourierLogin, createdCourierPassword);
    }

    @Test
    public void missingFieldReturnsError() {
        String body = "{\"login\":\"only_login_" + System.currentTimeMillis() + "\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(400);
    }

    @Test
    public void existingLoginReturnsError() {
        createdCourierLogin = "existing_user_" + System.currentTimeMillis();
        createdCourierPassword = "pass123";
        String body = "{\"login\":\"" + createdCourierLogin + "\",\"password\":\"" + createdCourierPassword + "\",\"firstName\":\"Ольга\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier");

        createdCourierId = getCourierId(createdCourierLogin, createdCourierPassword);

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}
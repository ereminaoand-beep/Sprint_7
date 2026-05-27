package courier;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {

    private String testLogin;
    private String testPassword;
    private int createdCourierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        testLogin = "logintest_" + System.currentTimeMillis();
        testPassword = "pass123";

        String body = "{\"login\":\"" + testLogin + "\",\"password\":\"" + testPassword + "\",\"firstName\":\"Тестовый\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier");

        createdCourierId = getCourierId(testLogin, testPassword);
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

    private Response sendLoginRequestWithRetry(String body) {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier/login");

        // Если сервер вернул 504, пробуем ещё 1 раз без задержки
        if (response.getStatusCode() == 504) {
            response = given()
                    .header("Content-Type", "application/json")
                    .body(body)
                    .post("/api/v1/courier/login");
        }
        return response;
    }

    @Test
    public void courierCanLogin() {
        String body = "{\"login\":\"" + testLogin + "\",\"password\":\"" + testPassword + "\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .and()
                .body("id", notNullValue());
    }

    @Test
    public void loginWithoutLoginFails() {
        String body = "{\"password\":\"" + testPassword + "\"}";

        Response response = sendLoginRequestWithRetry(body);
        response.then().statusCode(400);
    }

    @Test
    public void loginWithoutPasswordFails() {
        String body = "{\"login\":\"" + testLogin + "\"}";

        Response response = sendLoginRequestWithRetry(body);
        response.then().statusCode(400);
    }

    @Test
    public void wrongPasswordReturnsError() {
        String body = "{\"login\":\"" + testLogin + "\",\"password\":\"wrong_password\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
    }

    @Test
    public void wrongLoginReturnsError() {
        String body = "{\"login\":\"wrong_login\",\"password\":\"" + testPassword + "\"}";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
    }

    @Test
    public void successLoginReturnsId() {
        String body = "{\"login\":\"" + testLogin + "\",\"password\":\"" + testPassword + "\"}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/courier/login");

        response.then().statusCode(200);
        Integer id = response.then().extract().path("id");
        org.junit.Assert.assertNotNull(id);
        org.junit.Assert.assertTrue(id > 0);
    }
}
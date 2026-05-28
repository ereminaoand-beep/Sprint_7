package client;

import io.restassured.response.ValidatableResponse;
import org.example.model.Courier;
import org.example.model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient extends BaseClient {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String COURIER_LOGIN_PATH = "/api/v1/courier/login";

    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }

    public ValidatableResponse loginCourier(CourierCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then();
    }

    public ValidatableResponse deleteCourier(int courierId) {
        return given()
                .spec(getBaseSpec())
                .when()
                .delete(COURIER_PATH + "/" + courierId)
                .then();
    }

    // Метод для получения ID курьера после логина
    public Integer getCourierId(CourierCredentials credentials) {
        return loginCourier(credentials)
                .extract()
                .path("id");
    }
}
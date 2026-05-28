package client;

import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    private static final String ORDER_PATH = "/api/v1/orders";

    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    public ValidatableResponse getOrdersList() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    // Если нужна отмена заказа
    public ValidatableResponse cancelOrder(int trackId) {
        return given()
                .spec(getBaseSpec())
                .queryParam("track", trackId)
                .when()
                .put(ORDER_PATH + "/cancel")
                .then();
    }
}
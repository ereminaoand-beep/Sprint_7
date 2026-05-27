package order;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void getOrdersListReturnsList() {
        Response response = given()
                .get("/api/v1/orders");

        response.then()
                .statusCode(200)
                .and()
                .body("orders", notNullValue())
                .and()
                .body("orders", instanceOf(java.util.ArrayList.class));
    }
}
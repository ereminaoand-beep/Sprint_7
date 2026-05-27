package order;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final String color1;
    private final String color2;
    private List<Integer> createdOrderTracks;

    public OrderCreateTest(String color1, String color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        createdOrderTracks = new ArrayList<>();
    }

    @After
    public void tearDown() {
        System.out.println("Заказы созданы с tracks: " + createdOrderTracks);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"BLACK", null},      // только BLACK
                {null, "GREY"},       // только GREY
                {"BLACK", "GREY"},    // оба цвета
                {null, null}          // без цвета
        });
    }

    @Test
    public void createOrderWithDifferentColors() {
        String colorsArray = "[";
        if (color1 != null) colorsArray += "\"" + color1 + "\"";
        if (color2 != null) {
            if (color1 != null) colorsArray += ",";
            colorsArray += "\"" + color2 + "\"";
        }
        colorsArray += "]";

        String body = "{\n" +
                "  \"firstName\": \"Алексей\",\n" +
                "  \"lastName\": \"Смирнов\",\n" +
                "  \"address\": \"ул. Тестовая, 1\",\n" +
                "  \"metroStation\": 4,\n" +
                "  \"phone\": \"+79991234567\",\n" +
                "  \"rentTime\": 5,\n" +
                "  \"deliveryDate\": \"2025-12-31\",\n" +
                "  \"comment\": \"Тестовый заказ\",\n" +
                "  \"color\": " + colorsArray + "\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/api/v1/orders");

        int track = response.then().extract().path("track");
        createdOrderTracks.add(track);

        response.then()
                .statusCode(201)
                .and()
                .body("track", notNullValue());
    }
}
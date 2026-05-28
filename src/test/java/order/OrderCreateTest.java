package order;

import client.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.example.model.Order;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest extends BaseTest {

    private final List<String> colors;
    private final String scenarioName;
    private static final List<Integer> createdOrderTracks = new ArrayList<>();

    public OrderCreateTest(String scenarioName, List<String> colors) {
        this.scenarioName = scenarioName;
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"Только BLACK", List.of("BLACK")},
                {"Только GREY", List.of("GREY")},
                {"Оба цвета", Arrays.asList("BLACK", "GREY")},
                {"Без цвета", null},
                {"Пустой список", List.of()}
        });
    }

    @After
    @Step("Отмена всех созданных заказов")
    public void tearDown() {
        for (Integer track : createdOrderTracks) {
            orderClient.cancelOrder(track)
                    .statusCode(SC_OK);
        }
        createdOrderTracks.clear();
    }

    @Test
    @Step("Создание заказа с цветами: {scenarioName}")
    @DisplayName("Создание заказа с разными комбинациями цветов")
    @Description("Параметризованный тест: BLACK, GREY, оба, без цвета, пустой список")
    public void createOrderWithDifferentColors() {
        Order order = new Order(
                "Иван",
                "Петров",
                "ул. Ленина, 1",
                4,
                "+79991234567",
                5,
                "2025-06-01",
                "Позвонить за час",
                colors
        );

        int track = orderClient.createOrder(order)
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract()
                .path("track");

        createdOrderTracks.add(track);
    }
}
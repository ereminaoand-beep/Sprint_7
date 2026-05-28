package order;

import client.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class OrderListTest extends BaseTest {

    @Test
    @DisplayName("Получение списка заказов – успешный запрос")
    @Description("Проверка, что GET /api/v1/orders возвращает 200 и непустой массив orders")
    public void getOrdersListSuccessfully() {
        orderClient.getOrdersList()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders", instanceOf(ArrayList.class));
    }
}
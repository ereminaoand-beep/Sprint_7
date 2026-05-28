package courier;

import client.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.example.model.Courier;
import org.example.model.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CourierLoginTest extends BaseTest {

    private Courier createdCourier;
    private int createdCourierId;

    @Before
    public void setUp() {
        createdCourier = new Courier(
                "logintest_" + System.currentTimeMillis(),
                "pass123",
                "Тестовый"
        );

        courierClient.createCourier(createdCourier)
                .statusCode(SC_CREATED);

        CourierCredentials credentials = new CourierCredentials(
                createdCourier.getLogin(),
                createdCourier.getPassword()
        );
        createdCourierId = courierClient.getCourierId(credentials);
    }

    @After
    public void tearDown() {
        if (createdCourierId > 0) {
            courierClient.deleteCourier(createdCourierId)
                    .statusCode(SC_OK);
        }
    }

    @Step("Отправка запроса на логин и проверка статус-кода")
    private void sendLoginRequest(CourierCredentials credentials, int expectedStatusCode) {
        courierClient.loginCourier(credentials)
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Логин курьера - успешная авторизация")
    @Description("Проверка, что курьер может авторизоваться с правильными логином и паролем")
    public void courierCanLogin() {
        CourierCredentials credentials = new CourierCredentials(
                createdCourier.getLogin(),
                createdCourier.getPassword()
        );

        courierClient.loginCourier(credentials)
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин курьера - авторизация без логина")
    @Description("Проверка, что без логина авторизация невозможна, возвращается ошибка 400 и сообщение")
    public void loginWithoutLoginFails() {
        CourierCredentials credentialsWithoutLogin = new CourierCredentials(
                null,
                createdCourier.getPassword()
        );

        courierClient.loginCourier(credentialsWithoutLogin)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера - авторизация без пароля")
    @Description("Проверка, что без пароля авторизация невозможна, возвращается ошибка 400 и сообщение")
    public void loginWithoutPasswordFails() {
        CourierCredentials credentialsWithoutPassword = new CourierCredentials(
                createdCourier.getLogin(),
                null
        );

        courierClient.loginCourier(credentialsWithoutPassword)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера - неверный пароль")
    @Description("Проверка, что с неверным паролем авторизация невозможна, возвращается ошибка 404")
    public void wrongPasswordReturnsError() {
        CourierCredentials wrongCredentials = new CourierCredentials(
                createdCourier.getLogin(),
                "wrong_password"
        );

        courierClient.loginCourier(wrongCredentials)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - неверный логин")
    @Description("Проверка, что с неверным логином авторизация невозможна, возвращается ошибка 404")
    public void wrongLoginReturnsError() {
        CourierCredentials wrongCredentials = new CourierCredentials(
                "wrong_login",
                createdCourier.getPassword()
        );

        courierClient.loginCourier(wrongCredentials)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера - успешный запрос возвращает id")
    @Description("Проверка, что при успешной авторизации возвращается id курьера")
    public void successLoginReturnsId() {
        CourierCredentials credentials = new CourierCredentials(
                createdCourier.getLogin(),
                createdCourier.getPassword()
        );

        Integer id = courierClient.getCourierId(credentials);

        assertNotNull("ID не должен быть null", id);
        assertTrue("ID должен быть больше 0", id > 0);
    }
}
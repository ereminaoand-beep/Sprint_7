package courier;

import client.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.model.Courier;
import org.example.model.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest extends BaseTest {

    private Courier createdCourier;
    private int createdCourierId;

    @Before
    public void setUp() {
        createdCourier = new Courier(
                "create_test_" + System.currentTimeMillis(),
                "password123",
                "Создаваемый"
        );
    }

    @After
    public void tearDown() {
        if (createdCourierId > 0) {
            courierClient.deleteCourier(createdCourierId)
                    .statusCode(SC_OK);
        }
    }

    @Test
    @DisplayName("Создание курьера - успешное создание")
    @Description("Проверка, что курьера можно создать с валидными данными, возвращается 201 Created")
    public void createCourierSuccessfully() {
        courierClient.createCourier(createdCourier)
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        CourierCredentials credentials = new CourierCredentials(
                createdCourier.getLogin(),
                createdCourier.getPassword()
        );
        createdCourierId = courierClient.getCourierId(credentials);
    }

    @Test
    @DisplayName("Создание курьера - нельзя создать двух одинаковых")
    @Description("Попытка создать курьера с уже существующим логином возвращает 409 Conflict")
    public void createDuplicateCourierFails() {
        courierClient.createCourier(createdCourier)
                .statusCode(SC_CREATED);

        CourierCredentials credentials = new CourierCredentials(
                createdCourier.getLogin(),
                createdCourier.getPassword()
        );
        createdCourierId = courierClient.getCourierId(credentials);

        Courier sameCourier = new Courier(
                createdCourier.getLogin(),
                createdCourier.getPassword(),
                createdCourier.getFirstName()
        );

        courierClient.createCourier(sameCourier)
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Создание курьера - без логина (обязательное поле)")
    @Description("Проверка, что при отсутствии логина возвращается 400 Bad Request")
    public void createCourierWithoutLoginFails() {
        Courier courierWithoutLogin = new Courier(
                null,
                "password123",
                "Без логина"
        );

        courierClient.createCourier(courierWithoutLogin)
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание курьера - без пароля (обязательное поле)")
    @Description("Проверка, что при отсутствии пароля возвращается 400 Bad Request")
    public void createCourierWithoutPasswordFails() {
        Courier courierWithoutPassword = new Courier(
                "login_" + System.currentTimeMillis(),
                null,
                "Без пароля"
        );

        courierClient.createCourier(courierWithoutPassword)
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание курьера - без имени (необязательное поле)")
    @Description("Проверка, что курьера можно создать без имени — успешный ответ 201")
    public void createCourierWithoutFirstNameSuccess() {
        Courier courierWithoutFirstName = new Courier(
                "onlylogin_" + System.currentTimeMillis(),
                "pass",
                null
        );

        courierClient.createCourier(courierWithoutFirstName)
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        CourierCredentials credentials = new CourierCredentials(
                courierWithoutFirstName.getLogin(),
                courierWithoutFirstName.getPassword()
        );
        createdCourierId = courierClient.getCourierId(credentials);
    }
}
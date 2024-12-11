import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static api.CourierApi.BAD_LOGIN_REQUEST;
import static api.CourierApi.USER_WASNT_FOUND;
import static org.hamcrest.CoreMatchers.equalTo;


public class LoginCourierTests {

    private CourierData courier;
    private CourierApi courierApi;
    private Integer courierId;


    @Before
    public void setUp() {
        //Данные курьера
        courier = new CourierData("ChurchHellaASD", "12345");
        //API
        courierApi = new CourierApi();
        //Создаем курьера
        courierApi.createCourier(courier);
    }

    @After
    public void cleanUp(){
        try {
            //логинимся (данные курьера)
            if (courierId != null){
                //Удаляемся (извлеченный ID)
                courierApi.deleteCourier(courierId).log().all();
            }
        } catch (Exception e) {
            System.out.println("Failed to delete the courier");
        }
    }

    @Test
    @DisplayName("Успешный логин курьера со всеми данными")
    @Description("Код 200 и id соответсует значению в JSON")
    public void courierLoginAllParametersTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData(courier.getLogin(),courier.getPassword());

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //получаем id
        courierId = response.extract().jsonPath().getInt("id");

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(courierId));
    }

    @Test
    @DisplayName("Ошибка при логине с неверным паролем")
    @Description("Код 404 с текстом 'Учетная запись не найдена'")
    public void errorWithWrongPasswordTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData(courier.getLogin(), "asdc83l");

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo(USER_WASNT_FOUND));
    }

    @Test
    @DisplayName("Ошибка при логине без ввода пароля")
    @Description("Код 400 с текстом 'Недостаточно данных для входа'")
    public void errorWithNoPasswordTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData(courier.getLogin(), "");

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //Проверка где тест падает (тут баг)
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_LOGIN_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при логине без логина")
    @Description("Код 400 с текстом 'Недостаточно данных для входа'")
    public void errorWithNoLoginTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData("", courier.getPassword());

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //Проверка где тест падает (тут баг)
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_LOGIN_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при логине несуществуещего курьера")
    @Description("Код 404 с текстом 'Учетная запись не найдена'")
    public void errorWithNonExistingLoginTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData("iDontEx1stL0ginBlaBla", "asdc83l");

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //Проверка где тест падает (тут баг)
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo(USER_WASNT_FOUND));
    }

}

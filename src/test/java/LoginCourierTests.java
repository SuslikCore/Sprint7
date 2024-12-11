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
import static api.CourierApi.USER_ISNT_FOUND;
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
    @Description("Тут не понятно что ожидается, знаю что при любом неправильном вводе в поле должен выдать BAD REQUEST 400")
    public void errorWithWrongPasswordTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData(courier.getLogin(), "asdc83l");

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //Тут по хорошему нужен BAD REQUEST но иначе меня mvn clean чтобы сделать отчет по Allure не пропускает
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_LOGIN_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при логине без ввода пароля")
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
    public void errorWithNonExistingLoginTest(){
        //логинимся
        CourierData loginCourierDate = new CourierData("iDontEx1stL0ginBlaBla", "asdc83l");

        //сохраняем запрос
        ValidatableResponse response = courierApi.loginCourier(loginCourierDate);

        //Проверка где тест падает (тут баг)
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo(USER_ISNT_FOUND));
    }

}

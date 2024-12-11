import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static api.CourierApi.BAD_COURIER_REQUEST;
import static api.CourierApi.ERROR_COURIER_CONFLICT;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTests {

    private CourierData courier;
    private CourierApi courierApi;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
    }

    @After
    public void cleanUp(){

        //Получаем логин и пароль и сохраняем
        try {
            CourierData loginCourierDate = new CourierData(courier.getLogin(),courier.getPassword());

            //Достаем id с помощью метода и передаем туда логин и пароль
            Integer courierId  = courierApi.loginCourier(loginCourierDate).log().all().extract().jsonPath().getInt("id");

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
    @DisplayName("Курьер создается со всеми параметрами")
    @Description("Код 201 и булен true")
    public void courierCanBeCreatedAllParametersTest(){
        courier = new CourierData("Nachos", "123123","helloworld");

        // Создание курьера и сохранение ответа в response
        ValidatableResponse response = courierApi.createCourier(courier);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Курьер создается с необходимыми параметрами")
    @Description("Код 201 и булен true")
    public void courierCanBeCreatedWithRequiredParametersTest(){

        courier = new CourierData("HolyCowAsd123", "asf2xvf");



        // Создание курьера и сохранение ответа в response
        ValidatableResponse response = courierApi.createCourier(courier);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Возникает ошибка при создании уже существующего курьера")
    @Description("Код 409 с текстом 'Этот логин уже используется. Попробуйте другой.'")
    public void errorWhenTheCourierAlreadyExistsTest(){

        courier = new CourierData("HolyCowAsd123", "asf2xvf");
        courierApi.createCourier(courier);

        // Создание курьера и сохранение ответа в response
        ValidatableResponse response = courierApi.createCourier(courier);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", equalTo(ERROR_COURIER_CONFLICT));
    }

    @Test
    @DisplayName("Возникает ошибка при создании курьера только с логином")
    @Description("Код 400 и сообщение 'Недостаточно данных для создания учетной записи'")
    public void errorWithOnlyLoginFieldTest(){

        courier = new CourierData("HolyCowAsd123","");

        // Создание курьера и сохранение ответа в response
        ValidatableResponse response = courierApi.createCourier(courier);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_COURIER_REQUEST));
    }
    @Test
    @DisplayName("Возникает ошибка при создании курьера только с паролем")
    @Description("Код 400 и сообщение 'Недостаточно данных для создания учетной записи'")
    public void errorWithOnlyPasswordFieldTest(){

        courier = new CourierData("","asvy5j");

        // Создание курьера и сохранение ответа в response
        ValidatableResponse response = courierApi.createCourier(courier);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_COURIER_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при создании курьера только с необязательным полем")
    @Description("Код 400 и сообщение 'Недостаточно данных для создания учетной записи'")
    public void errorWithOnlyUnnecessaryInputTest(){
        courier = new CourierData("", "","helloworld");

        // Создание курьера и сохранение ответа в response
        ValidatableResponse response = courierApi.createCourier(courier);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_COURIER_REQUEST));
    }

}

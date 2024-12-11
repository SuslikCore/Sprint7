package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import static io.restassured.RestAssured.given;

public class CourierApi extends RestApi {

    public static final String CREATE_COURIER_URI = "/api/v1/courier";
    public static final String LOGIN_COURIER_URI = "/api/v1/courier/login";
    public static final String DELETE_COURIER_URI = "/api/v1/courier/{id}";
    public static final String ERROR_COURIER_CONFLICT = "Этот логин уже используется. Попробуйте другой.";
    public static final String BAD_COURIER_REQUEST = "Недостаточно данных для создания учетной записи";
    public static final String BAD_LOGIN_REQUEST = "Недостаточно данных для входа";
    public static final String USER_WASNT_FOUND = "Учетная запись не найдена";

    @Step("Создаем курьера")
    public ValidatableResponse createCourier(CourierData courier){
        return given()
                .spec(requestSpecification())
                .and()
                .body(courier)
                .when()
                .post(CREATE_COURIER_URI)
                .then();
    }

    @Step("Логинимся курьером")
    public ValidatableResponse loginCourier(CourierData courier){
        return given()
                .spec(requestSpecification())
                .and()
                .body(courier)
                .when()
                .post(LOGIN_COURIER_URI)
                .then();
    }

    @Step("Удаляем курьера")
    public ValidatableResponse deleteCourier(Integer courierId){
        return given()
                .spec(requestSpecification())
                .pathParams("id",courierId)
                .when()
                .delete(DELETE_COURIER_URI)
                .then();
    }

}

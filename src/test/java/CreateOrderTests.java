import api.CourierApi;
import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import model.OrderData;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private int orderId;
    private int courierId;
    private int orderTrackNumber;

    private CourierApi courierApi = new CourierApi();
    private OrderApi orderApi = new OrderApi();

    private String firstName;
    private String lastName;
    private String address;
    private int metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    public CreateOrderTests(String firstName, String lastName, String address, int metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Before
    public void setUp() {
        //Данные курьера
        CourierData courier = new CourierData("ChurchHellaMeow", "12345");

        //создаем курьера
        courierApi.createCourier(courier);

        //Логинимся и сохраняем тело
        ValidatableResponse courierResponse = courierApi.loginCourier(courier);

        //Сохраняем id курьера выводим логи
        courierId = courierResponse.log().all().extract().jsonPath().getInt("id");
    }

    @After
    public void cleanUp() {

        // Я решил написать код чтобы заказы закрывались, но требуется принять заказ сущ курьером сначала
        //Сохраняем тело с номером заказа в orderResponse
        ValidatableResponse orderResponse = orderApi.getOrderIdByTrackNumber(orderTrackNumber);

        //Сохраняем и выводим id заказа
        orderId = orderResponse.log().all().extract().jsonPath().getInt("order.id");

        //принимаем заказа и сохраняем ответ
        ValidatableResponse acceptResponse = orderApi.acceptOrder(courierId,orderId);
        acceptResponse.log().all();

        //Завершаем заказ
        try {
            ValidatableResponse finishOrderResponse = orderApi.finishOrderResponse(orderId);
            finishOrderResponse.log().all();
        } catch (Exception e) {
            System.out.println("Failed to finish the order");
        }

        //удаляем курьера
        try {
            ValidatableResponse deleteCourierResponse = courierApi.deleteCourier(courierId);
            deleteCourierResponse.log().all();
        } catch (Exception e) {
            System.out.println("Failed to delete the courier");
        }
    }

    @Parameterized.Parameters(name = "Тестовые данные: firstName = {0}, lastName = {1}, address = {2}, metroStation = {3}, phone = {4}, rentTime = {5}, deliveryDate = {6}, comment = {7}, color = {8}")
    public static Object[][] getDate(){
        return new Object[][]{
                {"Вася", "Васькин", "Москва", 3, "+7 800 355 35 35", 5, "2024-06-06", "БУ, Испугался", new String[]{"BLACK"}},
                {"Буся", "Пупкина", "Владивосток", 2, "+7 800 355 35 35", 3, "2024-06-06", "Не бойся", new String[]{"BLACK", "GREY"}},
                {"Куку", "Мяфкина", "Курятник", 2, "+7 800 355 35 35", 3, "2024-06-06", "Подойди сюда", new String[]{}}
        };
    }

    @Test
    @DisplayName("Успешное создание заказа с цветом, с двумя и без цвета")
    @Description("Код 201 и трек номер соответсует в json")
    public void orderCanBeCreatedAllParametersTest() {

        //Данные заказа
        OrderData order = new OrderData( firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        orderApi = new OrderApi();

        // Создание заказа и сохранение ответа в response
        ValidatableResponse response = orderApi.createOrder(order);

        //Получаем трек номер заказа
        orderTrackNumber = response.extract().jsonPath().getInt("track");
        System.out.println("Трек номер: " + orderTrackNumber);

        //Проверка
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("track", equalTo(orderTrackNumber));
    }
}

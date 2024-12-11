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
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderListTests {

    Integer courierId;
    Integer orderTrackNumber;
    Integer orderId;

    CourierApi courierApi = new CourierApi();
    OrderApi orderApi = new OrderApi();
    OrderData order;

    @Before
    public void setUp() {
        //Данные курьера и создаем курьера
        CourierData courier = new CourierData("ChurchHellaCall", "12345");
        courierApi.createCourier(courier);

        //Логинимся и сохраняем тело, Сохраняем id курьера
        ValidatableResponse courierResponse = courierApi.loginCourier(courier);
        courierId = courierResponse.extract().jsonPath().getInt("id");

        //Данные заказа и создание заказа и сохранение ответа в response
        order = new OrderData("Вася", "Васькин", "Москва", 3, "+7 800 355 35 35", 5, "2024-06-06", "БУ, Испугался", new String[]{"BLACK"});
        ValidatableResponse response = orderApi.createOrder(order);

        //Получаем трек номер заказа
        orderTrackNumber = response.extract().jsonPath().getInt("track");

        //Сохраняем тело с номером заказа в orderResponse
        ValidatableResponse orderResponse = orderApi.getOrderIdByTrackNumber(orderTrackNumber);

        //Сохраняем id заказа
        orderId = orderResponse.extract().jsonPath().getInt("order.id");

        //принимаем заказ и сохраняем ответ
        ValidatableResponse acceptResponse = orderApi.acceptOrder(courierId,orderId);
        acceptResponse.log().all();
    }

    @After
    public void cleanUp() {

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

    @Test
    @DisplayName("Получение списка всех заказов")
    @Description("Код 200 и заказы не Null")
    public void getOrderListTest() {
        //Вызываем лист заказов фильтруем по курьеру и сохраняем
        ValidatableResponse orderListResponse = orderApi.getOrderlist();

        //Проверка
        orderListResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders", notNullValue());
    }
}

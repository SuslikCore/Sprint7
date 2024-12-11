package api;

import io.restassured.response.ValidatableResponse;
import model.OrderData;
import static io.restassured.RestAssured.given;

public class OrderApi extends RestApi {

    public static final String CREATE_ORDER_URI = "/api/v1/orders";
    public static final String GET_ORDER_ID_BY_TRACK_NUMBER_URI = "/api/v1/orders/track";
    public static final String ACCEPT_ORDER_URI = "/api/v1/orders/accept/{order}";
    public static final String FINISH_ORDER_URI = "/api/v1/orders/finish/{id}";
    public static final String GET_ORDER_LIST = "/api/v1/orders";


    public ValidatableResponse createOrder(OrderData order){
        return given()
                .spec(requestSpecification())
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    public ValidatableResponse getOrderIdByTrackNumber(int trackNumber){
        return given()
                .spec(requestSpecification())
                .queryParam("t", trackNumber)
                .when()
                .get(GET_ORDER_ID_BY_TRACK_NUMBER_URI)
                .then();
    }

    public ValidatableResponse acceptOrder(int courierId, int orderId){
        return given()
                .spec(requestSpecification())
                .queryParam("courierId", courierId)
                .pathParams("order",orderId)
                .when()
                .put(ACCEPT_ORDER_URI)
                .then();
    }

    public ValidatableResponse finishOrderResponse(int orderId){
        return given()
                .spec(requestSpecification())
                .pathParams("id",orderId)
                .when()
                .put(FINISH_ORDER_URI)
                .then();
    }

    public ValidatableResponse getOrderlist(Integer courierId){
        return given()
                .spec(requestSpecification())
                .queryParam("courierId",courierId)
                .when()
                .get(GET_ORDER_LIST)
                .then();
    }
}
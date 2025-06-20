package client;

import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    // Создание заказа (авторизованный и неавторизованный)
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .basePath("/api/orders")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(order)
                .when()
                .post();
    }

    // Создание заказа без авторизации
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .basePath("/api/orders")
                .body(order)
                .when()
                .post();
    }
}

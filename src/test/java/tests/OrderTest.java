package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OrderTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private User testUser;
    private String accessToken;

    private final String validIngredientId = "61c0c5a71d1f82001bdaaa6d"; // пример ID

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();

        // Создаём нового пользователя
        String email = "test_" + UUID.randomUUID() + "@yandex.ru";
        testUser = new User(email, "password123", "TestUser");
        Response registerResponse = userClient.register(testUser);
        accessToken = registerResponse.then().extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание заказа с авторизацией и валидными ингредиентами")
    public void createOrderWithAuthTest() {
        Order order = new Order(Collections.singletonList(validIngredientId));
        Response response = orderClient.create(order, accessToken);
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без авторизации с валидными ингредиентами")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(Collections.singletonList(validIngredientId));
        Response response = orderClient.create(order);
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов. Ожидается ошибка 400.")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        Response response = orderClient.create(order, accessToken);
        response.then()
                .statusCode(400)
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Description("Получение списка заказов авторизованного пользователя")
    public void getOrdersWithAuthTest() {
        Response response = orderClient.getOrders(accessToken);
        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("success", is(true));
    }
}

package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import model.Order;
import model.User;
import org.junit.*;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;

public class OrderTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private String validIngredientId = "61c0c5a71d1f82001bdaaa6d"; // заменяем на реальный, если API выдаёт другой

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();

        // регистрируем и логиним пользователя
        String email = "test_" + UUID.randomUUID() + "@yandex.ru";
        User user = new User(email, "password123", "OrderTester");
        userClient.register(user);
        Response login = userClient.login(user);
        accessToken = login.then().extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание заказа с авторизацией и валидными ингредиентами")
    public void createOrderWithAuthAndIngredientsTest() {
        Order order = new Order(Arrays.asList(validIngredientId));
        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(200).body("success", is(true));
    }

    @Test
    @Description("Создание заказа без авторизации и с валидными ингредиентами")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(Arrays.asList(validIngredientId));
        Response response = orderClient.createOrderWithoutAuth(order);
        response.then().statusCode(200).body("success", is(true));
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(400).body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с невалидным ингредиентом")
    public void createOrderWithInvalidIngredientTest() {
        Order order = new Order(Arrays.asList("invalid_id"));
        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(500); // API возвращает 500 по документации
    }

    @Test
    @Description("Создание заказа с авторизацией, но с несколькими ингредиентами")
    public void createOrderWithMultipleIngredientsTest() {
        Order order = new Order(Arrays.asList(validIngredientId, validIngredientId));
        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(200).body("success", is(true));
    }
}

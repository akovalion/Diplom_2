package tests;

import client.UserClient;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;

public class UserRegisterTest {

    private UserClient userClient;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        String email = "test_" + UUID.randomUUID() + "@yandex.ru";
        testUser = new User(email, "password123", "TestUser");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя. Успешная регистрация.")
    public void createUniqueUserTest() {
        Response response = userClient.register(testUser);
        response.then().statusCode(200).body("success", is(true));
        accessToken = response.then().extract().path("accessToken");
    }

    @Test
    @Description("Создание уже существующего пользователя. Ожидается ошибка 403.")
    public void createDuplicateUserTest() {
        userClient.register(testUser);
        Response response = userClient.register(testUser);
        response.then().statusCode(403)
                .body("message", is("User already exists"))
                .body("success", is(false));
    }

    @Test
    @Description("Создание пользователя без email. Ожидается ошибка 403.")
    public void createUserWithoutEmail() {
        User user = new User(null, "password123", "TestUser");
        Response response = userClient.register(user);
        response.then().statusCode(403)
                .body("message", is("Email, password and name are required fields"))
                .body("success", is(false));
    }

    @Test
    @Description("Создание пользователя без пароля. Ожидается ошибка 403.")
    public void createUserWithoutPassword() {
        User user = new User(testUser.getEmail(), null, "TestUser");
        Response response = userClient.register(user);
        response.then().statusCode(403)
                .body("message", is("Email, password and name are required fields"))
                .body("success", is(false));
    }

    @Test
    @Description("Создание пользователя без имени. Ожидается ошибка 403.")
    public void createUserWithoutName() {
        User user = new User(testUser.getEmail(), "password123", null);
        Response response = userClient.register(user);
        response.then().statusCode(403)
                .body("message", is("Email, password and name are required fields"))
                .body("success", is(false));
    }
}

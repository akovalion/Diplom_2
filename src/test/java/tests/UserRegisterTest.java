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
        if (testUser != null) {
            // Пытаемся залогиниться и получить accessToken
            Response loginResponse = userClient.login(testUser);
            accessToken = loginResponse.then().extract().path("accessToken");
            if (accessToken != null) {
                userClient.delete(accessToken);
            }
        }
    }

    @Test
    @Description("Создание уникального пользователя. Ожидается успешная регистрация.")
    public void createUniqueUserTest() {
        Response response = userClient.register(testUser);
        response.then()
                .statusCode(200)
                .body("success", is(true));
        // accessToken НЕ получаем здесь — он будет получен в @After
    }

    @Test
    @Description("Создание уже зарегистрированного пользователя. Ожидается ошибка 403.")
    public void createDuplicateUserTest() {
        userClient.register(testUser);
        Response duplicateResponse = userClient.register(testUser);
        duplicateResponse.then()
                .statusCode(403)
                .body("message", is("User already exists"))
                .body("success", is(false));
    }

    @Test
    @Description("Создание пользователя без обязательного поля. Ожидается ошибка 403.")
    public void createUserWithoutRequiredFieldTest() {
        User incompleteUser = new User(null, "password123", "TestUser"); // без email
        Response response = userClient.register(incompleteUser);
        response.then()
                .statusCode(403)
                .body("message", is("Email, password and name are required fields"))
                .body("success", is(false));
    }
}

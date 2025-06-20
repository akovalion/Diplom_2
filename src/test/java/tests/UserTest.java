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
import static org.hamcrest.Matchers.notNullValue;

public class UserTest {

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
    @Description("Создание уникального пользователя. Ожидается успешная регистрация с получением accessToken.")
    public void createUniqueUserTest() {
        Response response = userClient.register(testUser);
        response.then()
                .statusCode(200)
                .body("success", is(true));
        accessToken = response.then().extract().path("accessToken");
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
    @Description("Создание пользователя без одного обязательного поля. Ожидается ошибка 403.")
    public void createUserWithoutRequiredFieldTest() {
        User incompleteUser = new User(null, "password123", "TestUser"); // без email
        Response response = userClient.register(incompleteUser);
        response.then()
                .statusCode(403)
                .body("message", is("Email, password and name are required fields"))
                .body("success", is(false));
    }

    @Test
    @Description("Успешный вход под существующим пользователем.")
    public void loginExistingUserTest() {
        userClient.register(testUser);
        Response loginResponse = userClient.login(testUser);
        loginResponse.then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("success", is(true));
        accessToken = loginResponse.then().extract().path("accessToken");
    }

    @Test
    @Description("Вход с неверным логином или паролем. Ожидается ошибка 401.")
    public void loginWithWrongCredentialsTest() {
        User wrongUser = new User("wronguser@yandex.ru", "wrongpassword", "NoName");
        Response response = userClient.login(wrongUser);
        response.then()
                .statusCode(401)
                .body("message", is("email or password are incorrect"))
                .body("success", is(false));
    }
}

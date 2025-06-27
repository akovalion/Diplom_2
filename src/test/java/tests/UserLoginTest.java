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

public class UserLoginTest {

    private UserClient userClient;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
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
    @Description("Успешный вход под существующим пользователем.")
    public void loginExistingUserTest() {
        Response loginResponse = userClient.login(testUser);
        loginResponse.then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("success", is(true));
    }

    @Test
    @Description("Вход с неверным логином. Ожидается ошибка 401.")
    public void loginWithWrongEmailTest() {
        User wrongEmailUser = new User("wrong_" + testUser.getEmail(), testUser.getPassword(), testUser.getName());
        Response response = userClient.login(wrongEmailUser);
        response.then()
                .statusCode(401)
                .body("message", is("email or password are incorrect"))
                .body("success", is(false));
    }

    @Test
    @Description("Вход с неверным паролем. Ожидается ошибка 401.")
    public void loginWithWrongPasswordTest() {
        User wrongPasswordUser = new User(testUser.getEmail(), "wrongPassword123", testUser.getName());
        Response response = userClient.login(wrongPasswordUser);
        response.then()
                .statusCode(401)
                .body("message", is("email or password are incorrect"))
                .body("success", is(false));
    }
}

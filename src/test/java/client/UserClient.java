package client;

import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    // Создание пользователя
    public Response register(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    // Логин пользователя
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    // Удаление пользователя (только с accessToken)
    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .delete("/api/auth/user");
    }
}

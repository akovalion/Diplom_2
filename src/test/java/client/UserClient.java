package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    @Step("Создание пользователя")
    public Response register(User user) {
        return getSpec()
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Логин пользователя")
    public Response login(User user) {
        return getSpec()
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя")
    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete("https://stellarburgers.nomoreparties.site/api/auth/user");
    }
}

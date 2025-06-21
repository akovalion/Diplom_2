package client;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class BaseClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    protected RequestSpecification getSpec() {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json");
    }
}

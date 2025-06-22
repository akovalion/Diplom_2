package client;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.requestSpecification;
import static io.restassured.RestAssured.with;

public abstract class BaseClient {

    protected RequestSpecification getBaseSpec() {
        return with()
                .baseUri("https://stellarburgers.nomoreparties.site")
                .header("Content-type", "application/json");
    }
}

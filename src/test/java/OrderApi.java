import io.restassured.response.Response;


import static io.restassured.RestAssured.given;

public class OrderApi {

    private final String apiRequest = "/api/v1/orders";

    public Response getOrder() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(apiRequest);
    }
}



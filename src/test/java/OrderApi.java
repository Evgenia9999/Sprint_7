import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONObject;


import static io.restassured.RestAssured.given;

public class OrderApi {

    private final String apiRequest = "/api/v1/orders";


    @Step("Get Order")
    public Response getOrder() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(apiRequest);
    }

    @Step("Get Order List")
    public Response getOrderList(JSONObject orderRequest) {
        return given()
                .header("Content-type", "application/json")
                .body(orderRequest.toString())
                .when()
                .post("/api/v1/orders");
    }
}



import io.restassured.response.Response;
import java.io.File;
import static io.restassured.RestAssured.given;

public class CourierApi {

    private final String apiRequest = "/api/v1/courier";

    public Response createCourier(Object json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(apiRequest);
    }

    private final String requestLogin = "/api/v1/courier/login";

    public Response courierLogin(Object json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(requestLogin);
    }

}

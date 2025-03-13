import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierApi {

    private final String apiRequest = "/api/v1/courier";
    private final String requestLogin = "/api/v1/courier/login";

    @Step("Create Courier")
    public Response createCourier(Object json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(apiRequest);
    }

    @Step("Login Courier")
    public Response courierLogin(Object json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(requestLogin);
    }

    @Step("Delete Courier")
    public void courierDelete(int courierId) {

        given()
                .header("Content-type", "application/json")
                .when()
                .delete(apiRequest + courierId)
                .then()
                .statusCode(SC_OK);

    }
}

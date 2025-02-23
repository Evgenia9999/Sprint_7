import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.jupiter.api.DisplayName;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationTest { // Removed 'static'
    private final List<String> colors;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    // Corrected constructor name to match class name
    public OrderCreationTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {List.of("BLACK")},
                {List.of("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {List.of()}
        });
    }

    @Test
    @DisplayName("Get orders list test")
    public void getOrdersListTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @Test
    @DisplayName("Get orders list and verify detailed structure")
    public void getOrdersDetailedTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("orders[0].id", instanceOf(Number.class))
                .body("orders.find { it.color != null }.color", instanceOf(List.class));
    }

    @Test
    @DisplayName("Create order with different color combinations")
    public void createOrderTest() {
        try {
            File jsonFile = new File("src/test/resources/orderPayload.json");
            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));
            JSONObject orderRequest = new JSONObject(jsonContent);

            if (colors.isEmpty()) {
                orderRequest.remove("color");
            } else {
                orderRequest.put("color", new JSONArray(colors));
            }

            Response response = given()
                    .header("Content-type", "application/json")
                    .body(orderRequest.toString())
                    .when()
                    .post("/api/v1/orders");

            response.then()
                    .assertThat()
                    .statusCode(201)
                    .body("track", notNullValue());

        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON file: " + e.getMessage());
    }
}
}
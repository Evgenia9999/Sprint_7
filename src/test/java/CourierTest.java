import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(OrderAnnotation.class)
public class CourierTest {
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            // Delete the courier if ID exists
            given()
                    .header("Content-type", "application/json")
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);

            courierId = null;
        }
    }

    @Test
    @Order(1)
    @DisplayName("Create new courier")
    public void createCourierTest() {
        File json = new File("src/test/resources/courierCardFullData.json");

        Response createResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier");

        createResponse.then()
                .assertThat()
                .body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("Verify duplicate courier creation fails")
    public void verifyDuplicateCourierTest() {
        File json = new File("src/test/resources/courierCardFullData.json");

        Response duplicateResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier");

        duplicateResponse.then()
                .assertThat()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @Test
    @Order(3)
    @DisplayName("Error shown when field is missing")
    public void errorWhenFieldIsMissingTest() {
        File json = new File("src/test/resources/courierCardMissingData.json");

        Response createResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier");

        createResponse.then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    @Order(4)
    @DisplayName("Login and get courier ID")
    public void loginAndGetIdTest() {
        File json = new File("src/test/resources/courierLogin.json");

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login");

        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue());

        // Store courier ID for cleanup
        courierId = loginResponse.path("id");
    }

    @Test
    @Order(5)
    @DisplayName("Check error for incorrect input")
    public void checkErrorForIncorrectInputTest() {
        File json = new File("src/test/resources/IncorrectIpCourierLogin.json");

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login");

        loginResponse.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));

    }

    @Test
    @Order(5)
    @DisplayName("Check error for empty input")
    public void checkErrorForEmptyInputTest() {
        File json = new File("src/test/resources/CourierLoginEmptyInput.json");

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login");

        loginResponse.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));

    }
}
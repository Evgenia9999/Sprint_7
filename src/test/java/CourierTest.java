import io.restassured.response.Response;
import model.CurierFullData;
import org.junit.After;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.apache.http.HttpStatus.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CourierTest extends BaseTest {
    private Integer courierId;


    @After
    public void cleanUp() {
        if (courierId != null) {
            // Delete the courier if ID exists
            given()
                    .header("Content-type", "application/json")
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(SC_OK);
            courierId = null;

        }
    }

    @Test
    public void test1_createCourier() {
        CurierFullData fullData = new CurierFullData("RandomUser2", "2111", "Indian Spice 000");

        CourierApi courierHelper = new CourierApi();
        Response createResponse = courierHelper.createCourier(fullData);
        createResponse.then()
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("ok", equalTo(true));

    }

    @Test
    public void test2_verifyDuplicateCourier() {
        CurierFullData fullData = new CurierFullData("RandomUser2", "2111", "Indian Spice 000");

        CourierApi courierHelper = new CourierApi();
        Response duplicateResponse = courierHelper.createCourier(fullData);

        duplicateResponse.then()
                .assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    public void test3_errorWhenFieldIsMissing() {
        CurierFullData fullData = new CurierFullData(null, "1234123", "Indian Spice 123");

        CourierApi courierHelper = new CourierApi();
        Response createResponse = courierHelper.createCourier(fullData);
        createResponse.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void test4_errorWhenOnlyPasswordIsSent() {
        CurierFullData fullData = new CurierFullData(null, "1234123", null);

        CourierApi courierHelper = new CourierApi();
        Response createResponse = courierHelper.createCourier(fullData);
        createResponse.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void test5_loginAndGetId() {
       // CurierFullData fullData = new CurierFullData("RandomUser2", "2111", "Indian Spice 000");
        CurierFullData data = new CurierFullData("RandomUser2", "2111", null);

        CourierApi courierHelper = new CourierApi();
        //courierHelper.createCourier(fullData);
        Response loginResponse = courierHelper.courierLogin(data);
        loginResponse.then()
                .statusCode(SC_OK) // Changed from SC_CREATED to SC_OK (200)
                .body("id", notNullValue());

        // Store courier ID for cleanup
        courierId = loginResponse.path("id");

    }

    @Test
    public void test6_checkErrorForIncorrectInput() {
        CurierFullData data = new CurierFullData("RandomUser123", "999000", null);

        CourierApi courierHelper = new CourierApi();
        Response loginResponse = courierHelper.courierLogin(data);
        loginResponse.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void test7_checkErrorForIncorrectPassword() {
        CurierFullData jsonWrongPassword = new CurierFullData("RandomUser", "999", null);
        CurierFullData jsonCreateCourier = new CurierFullData("RandomUser", "1234123", "Indian Spice 123");

        CourierApi courierHelper = new CourierApi();
        courierHelper.createCourier(jsonCreateCourier);
        Response loginResponse = courierHelper.courierLogin(jsonWrongPassword);

        loginResponse.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void test8_checkErrorForIncorrectLogin() {
        CurierFullData jsonCreateCourier = new CurierFullData("RandomUser", "111", "Indian Spice 123");
        CurierFullData jsonWrongLogin = new CurierFullData("Random", "1234123", null);

        CourierApi courierHelper = new CourierApi();
        courierHelper.createCourier(jsonCreateCourier);
        Response loginResponse = courierHelper.courierLogin(jsonWrongLogin);

        loginResponse.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void test9_checkErrorForEmptyInput() { // Renamed from test5_ to test6_ to avoid duplicate method name

        CurierFullData data = new CurierFullData(null, "111", null);

        CourierApi courierHelper = new CourierApi();
        Response loginResponse = courierHelper.courierLogin(data);
        loginResponse.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
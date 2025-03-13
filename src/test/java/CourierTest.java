import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CurierFullData;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.apache.http.HttpStatus.*;


public class CourierTest extends BaseTest {
    private Integer courierId;


    @After
    public void cleanUp() {
        if (courierId != null) {

            CourierApi courierHelper = new CourierApi();
            courierHelper.courierDelete(courierId);

            courierId = null;

        }
    }


    @Test
    public void testCreateCourier() {
        CurierFullData fullData = prepareCourierData("RandomUser555", "211555", "Indian Spice 001");

        CourierApi courierHelper = new CourierApi();
        Response createResponse = courierHelper.createCourier(fullData);

        verifyCreateCourierResponse(createResponse);
    }


    @Test
    public void testVerifyDuplicateCourier() {
        CurierFullData fullData = prepareCourierData("RandomUser333", "211333", "Indian Spice 000");

        CourierApi courierHelper = new CourierApi();
        Response duplicateResponse = courierHelper.createCourier(fullData);

        verifyDuplicateCourierResponse(duplicateResponse);
    }

    @Test
    public void testErrorWhenFieldIsMissing() {
        CurierFullData fullData = prepareCourierData(null, "1234123", "Indian Spice 123");

        CourierApi courierHelper = new CourierApi();
        Response createResponse = courierHelper.createCourier(fullData);

        verifyFieldMissingResponse(createResponse);
    }

    @Test
    public void testErrorWhenOnlyPasswordIsSent() {
        CurierFullData fullData = prepareCourierData(null, "1234123", null);

        CourierApi courierHelper = new CourierApi();
        Response createResponse = courierHelper.createCourier(fullData);

        verifyFieldMissingResponse(createResponse);
    }

    @Test
    public void testLoginAndGetId() {
        CurierFullData data = prepareCourierData("RandomUser333", "211333", null);

        CourierApi courierHelper = new CourierApi();
        Response loginResponse = courierHelper.courierLogin(data);

        verifyLoginResponse(loginResponse);

        // Store courier ID for cleanup
        courierId = loginResponse.path("id");
    }

    @Test
    public void testCheckErrorForIncorrectInput() {
        CurierFullData data = prepareCourierData("RandomUser123", "999000", null);

        CourierApi courierHelper = new CourierApi();
        Response loginResponse = courierHelper.courierLogin(data);

        verifyIncorrectInputResponse(loginResponse);
    }

    @Test
    public void testCheckErrorForIncorrectPassword() {
        CurierFullData jsonCreateCourier = prepareCourierData("RandomUser", "1234123", "Indian Spice 123");
        CurierFullData jsonWrongPassword = prepareCourierData("RandomUser", "999", null);

        CourierApi courierHelper = new CourierApi();
        courierHelper.createCourier(jsonCreateCourier);
        Response loginResponse = courierHelper.courierLogin(jsonWrongPassword);

        verifyIncorrectPasswordResponse(loginResponse);
    }

    @Test
    public void testCheckErrorForIncorrectLogin() {
        CurierFullData jsonCreateCourier = prepareCourierData("RandomUser", "111", "Indian Spice 123");
        CurierFullData jsonWrongLogin = prepareCourierData("Random", "1234123", null);

        CourierApi courierHelper = new CourierApi();
        courierHelper.createCourier(jsonCreateCourier);
        Response loginResponse = courierHelper.courierLogin(jsonWrongLogin);

        verifyIncorrectLoginResponse(loginResponse);
    }

    @Test
    public void testCheckErrorForEmptyInput() {
        CurierFullData data = prepareCourierData(null, "111", null);

        CourierApi courierHelper = new CourierApi();
        Response loginResponse = courierHelper.courierLogin(data);

        verifyEmptyInputResponse(loginResponse);
    }

    @Step("Prepare courier data with login: {login}, password: {password}, and name: {name}")
    private CurierFullData prepareCourierData(String login, String password, String name) {
        return new CurierFullData(login, password, name);
    }

    @Step("Verify create courier response")
    private void verifyCreateCourierResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("ok", equalTo(true));
    }

    @Step("Verify duplicate courier response")
    private void verifyDuplicateCourierResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Verify response for missing field")
    private void verifyFieldMissingResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Verify login response and extract courier ID")
    private void verifyLoginResponse(Response response) {
        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Step("Verify response for incorrect input")
    private void verifyIncorrectInputResponse(Response response) {
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Verify response for incorrect password")
    private void verifyIncorrectPasswordResponse(Response response) {
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Verify response for incorrect login")
    private void verifyIncorrectLoginResponse(Response response) {
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Verify response for empty input")
    private void verifyEmptyInputResponse(Response response) {
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderCreationTest extends BaseTest { // Removed 'static'


    @Test
    public void getOrdersListTest() {
        OrderApi orderHelper = new OrderApi();
        Response response = orderHelper.getOrder();

        verifyOrdersListResponse(response);
    }

    @Test
    public void getOrdersDetailedTest() {
        OrderApi orderHelper = new OrderApi();
        Response response = orderHelper.getOrder();

        verifyOrdersDetailedResponse(response);
    }


    @Step("Verify orders list response")
    private void verifyOrdersListResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @Step("Get orders detailed")
    private Response getOrdersDetailed() {
        OrderApi orderHelper = new OrderApi();
        return orderHelper.getOrder();
    }

    @Step("Verify orders detailed response")
    private void verifyOrdersDetailedResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders[0].id", instanceOf(Number.class))
                .body("orders.find { it.color != null }.color", instanceOf(List.class));
    }
}
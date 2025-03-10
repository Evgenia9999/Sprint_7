import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderCreationTest extends BaseTest { // Removed 'static'


    @Test
    public void getOrdersListTest() {
        OrderApi orderHelper = new OrderApi();
        Response response = orderHelper.getOrder();

        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @Test
    public void getOrdersDetailedTest() {
        OrderApi orderHelper = new OrderApi();
        Response response = orderHelper.getOrder();

        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders[0].id", instanceOf(Number.class))
                .body("orders.find { it.color != null }.color", instanceOf(List.class));
    }
}
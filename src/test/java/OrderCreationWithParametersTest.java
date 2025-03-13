import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.PayLoad;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class OrderCreationWithParametersTest extends BaseTest { // Removed 'static'
    private final List<String> colors;

    public OrderCreationWithParametersTest(List<String> colors) {
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
    public void createOrderTest() {
        try {
            //File jsonFile = new File("src/test/resources/orderPayload.json");
            PayLoad fullData = new PayLoad("Naruto",
                    "Uchiha",
                    "Konoha, 142 apt.",
                    "4",
                    "+7 800 355 35 35",
                    "5",
                    "2020-06-06",
                    "Saske, come back to Konoha",
                    "BLACK");

            JSONObject orderRequest = new JSONObject(fullData);



            if (colors.isEmpty()) {
                orderRequest.remove("color");
            } else {
                orderRequest.put("color", new JSONArray(colors));
            }


            OrderApi orderHelper = new OrderApi();
            Response response = orderHelper.getOrderList(orderRequest);

            verifyResponseIsNotNull(response);

        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON file: " + e.getMessage());
        }
    }

    @Step("Verify that response is not null")
    private void verifyResponseIsNotNull(Response response) {
        response.then()
                .assertThat()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }
}
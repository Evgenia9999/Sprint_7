import org.junit.Before;
import io.restassured.RestAssured;


public class BaseTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }
}
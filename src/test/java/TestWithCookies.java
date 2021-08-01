import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class TestWithCookies {
    @Test
    public void apiTestWithCookies() {
        step("Get cookie by api and add to cart", () -> {
            String authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body("addtocart_31.EnteredQuantity=3")
                            .when()
                            .post("http://demowebshop.tricentis.com/addproducttocart/details/31/1")
                            .then()
                            .statusCode(200)
                            .body("success", is(true))
                            .body("updatetopcartsectionhtml", is("(3)"))
                            .extract().cookie("Nop.customer");

            step("Set cookie to browser", () -> {
                open("http://demowebshop.tricentis.com/");
                Cookie ck = new Cookie("Nop.customer",authorizationCookie);
                getWebDriver().manage().addCookie(ck);
                refresh();
            });

            step("Check the cart", () -> {
                $(".cart-qty").shouldHave(text("3"));
            });
        });
    }
}
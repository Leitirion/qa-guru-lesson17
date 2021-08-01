import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class SetCookiesTests {

    @Test
    void setCookieTest() {

        step("Получить куки и подставить в браузер", () -> {
            String authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&" +
                                    "product_attribute_72_3_20=58&addtocart_72.EnteredQuantity=5")
                            .when()
                            .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                            .then()
                            .statusCode(200)
                            .extract()
                            .cookie("Nop.customer");

            step("Открыть небольшой контент, чтобы была сессия браузера, куда можно подставить куки", () ->
                    open("http://demowebshop.tricentis.com/Themes/DefaultClean/Content/images/mobile-menu-collapse.png"));

            step("Подставить куки в браузер", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("Nop.customer", authorizationCookie)));
        });

        step("Открыть веб интерфейс", () ->
                open("http://demowebshop.tricentis.com/"));

        step("Проверить количество товаров", () -> {
            $(".cart-qty").shouldHave(text("5"));
        });
    }

    @Test
    void setFullCookieTest() {

        step("Получить куки и подставить в браузер", () -> {
            Map<String, String> cookies = given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&" +
                            "product_attribute_72_3_20=58&addtocart_72.EnteredQuantity=7")
                    .when()
                    .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                    .then()
                    .statusCode(200)
                    .extract()
                    .cookies();


            step("Открыть небольшой контент, чтобы была сессия браузера, куда можно подставить куки", () ->
                    open("http://demowebshop.tricentis.com/Themes/DefaultClean/Content/images/mobile-menu-collapse.png"));

            step("Подставить полные куки в браузер", () -> {
                cookies.entrySet()
                        .stream()
                        .forEach(cookie -> WebDriverRunner.getWebDriver()
                                .manage().addCookie(new Cookie(cookie.getKey(), cookie.getValue())));

            });
        });

        step("Открыть веб интерфейс и убедиться, что кука подставилась успешно, товар добавлен в корзину", () ->
                open("http://demowebshop.tricentis.com/"));

        step("Проверить количество товаров", () -> {
            $(".cart-qty").shouldHave(text("7"));
        });

    }
}

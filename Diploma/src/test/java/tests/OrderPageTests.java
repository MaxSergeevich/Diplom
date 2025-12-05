package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.CartPage;
import pages.CheckoutPage;
import pages.MainPage;
import pages.ProductPage;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class OrderPageTests {
    private WebDriver driver;
    private WebDriverWait wait;

    String product = "Apple Watch 6"; // текст названия товара

    // Значения для полей при оформлении заказа
    String textFirstName = "Максим";
    String textLastName = "Серов";
    String textAddress = "Ленина, 4";
    String textCity = "Орлов";
    String textState = "Московская";
    String textPostcode = "630630";
    String textPhone = "89994590122";
    String textEmail = "tester@mail4428.ru";

    // Сертификат
    String textDiscount = "SERT500";
    BigDecimal numberDiscount = new BigDecimal(500);



    @BeforeEach
    public void setUp()
    {
        System.setProperty("webdriver.chrome.driver", "drivers//chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.navigate().to("https://intershop5.skillbox.ru");

        // Выполняем авторизацию
        driver.findElement(By.xpath("//div[@class='login-woocommerce']")).click();
        driver.findElement(By.xpath("//input[@name='username']")).sendKeys("maxtester4428");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("qwerty1111");
        driver.findElement(By.xpath("//button[@name='login']")).click();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    // 1. Добавить товар в корзину. Перейти к оформлению заказа. Заполнить обязательные поля. Выбрать оплата при доставке. Проверить почту, сумму, метод оплаты, заголовок.
    @Test
    public void orderProductWithFilledParamsAndDirectPayment_displayedPositiveResultTest() {
        // arrange
        MainPage mainPage = new MainPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // act
        mainPage.searchFormLocator.sendKeys(product); // вводим названием продукта в окно поиска
        mainPage.searchFormButtonLocator.click(); // нажимаем кнопку поиска
        productPage.addToCartButtonLocator.click(); // нажимаем "В корзину"
        productPage.detailsLocator.click(); // нажимаем подробнее для перехода в корзину

        String textAmountForPayment = cartPage.amountForPaymentLocator.getText(); // запоминаем сумму к оплате
        textAmountForPayment = textAmountForPayment.replace(",", ".").replace("₽", ""); // меняем запятую на точку, убираем знак рубля
        BigDecimal numberAmountForPayment = new BigDecimal(textAmountForPayment); // переводим в формат BigDecimal

        cartPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" в корзине
        checkoutPage.firstNameLocator.sendKeys(textFirstName); // заполняем поле имени
        checkoutPage.lastNameLocator.sendKeys(textLastName); // заполняем поле фамилии
        checkoutPage.addressLocator.sendKeys(textAddress); // заполняем поле адреса
        checkoutPage.cityLocator.sendKeys(textCity); // заполняем поле города
        checkoutPage.stateLocator.sendKeys(textState); // заполняем поле области
        checkoutPage.postcodeLocator.sendKeys(textPostcode); // заполняем поле индекса
        checkoutPage.phoneLocator.sendKeys(textPhone); // заполняем поле телефона
        checkoutPage.paymentMethodeUponDeliveryLocator.click(); // выбираем способ оплаты "Оплата при доставке"
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButtonLocator));
        checkoutPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" на странице оформления заказа

        String textCheckingEmail = checkoutPage.checkingEmailLocator.getText(); // получаем почту из подтверждения

        String textCheckingAmount = checkoutPage.checkingAmountLocator.getText(); // запоминаем сумму к оплате из подтверждения
        textCheckingAmount = textCheckingAmount.replace(",", ".").replace("₽", ""); // меняем запятую на точку, убираем знак рубля
        BigDecimal numberCheckingAmount = new BigDecimal(textCheckingAmount); // переводим в формат BigDecimal

        String textCheckingPaymentMethod = checkoutPage.checkingPaymentMethodLocator.getText().toLowerCase(); // получаем метод оплаты из подтверждения
        String textApprove = checkoutPage.approveLocator.getText(); // получаем заголовок из подтверждения

        // assert
        Assertions.assertAll(
                // проверка заголовка:
                () -> Assertions.assertEquals("Заказ получен", textApprove, "Заказ не получен"),
                // проверка почты:
                () -> Assertions.assertEquals(textEmail, textCheckingEmail, "Мейл не совпадает"),
                // проверка суммы к оплате:
                () -> Assertions.assertEquals(numberAmountForPayment, numberCheckingAmount, "Сумма к оплате не совпадает"),
                // проверка метода оплаты:
                () -> Assertions.assertEquals("оплата при доставке", textCheckingPaymentMethod, "Сумма к оплате не совпадает")
        );
    }

    // 2. Добавить товар в корзину. Перейти к оформлению заказа. Заполнить обязательные поля. Выбрать оплату прямым банковским переводом. Проверить почту, сумму, метод оплаты, заголовок.
    @Test
    public void orderProductWithFilledParamsAndBankPayment_displayedPositiveResultTest() {
        // arrange
        MainPage mainPage = new MainPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // act
        mainPage.searchFormLocator.sendKeys(product); // вводим названием продукта в окно поиска
        mainPage.searchFormButtonLocator.click(); // нажимаем кнопку поиска
        productPage.addToCartButtonLocator.click(); // нажимаем "В корзину"
        productPage.detailsLocator.click(); // нажимаем подробнее для перехода в корзину

        String textAmountForPayment = cartPage.amountForPaymentLocator.getText(); // запоминаем сумму к оплате
        textAmountForPayment = textAmountForPayment.replace(",", ".").replace("₽", ""); // меняем запятую на точку, убираем знак рубля
        BigDecimal numberAmountForPayment = new BigDecimal(textAmountForPayment); // переводим в формат BigDecimal

        cartPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" в корзине
        checkoutPage.firstNameLocator.sendKeys(textFirstName); // заполняем поле имени
        checkoutPage.lastNameLocator.sendKeys(textLastName); // заполняем поле фамилии
        checkoutPage.addressLocator.sendKeys(textAddress); // заполняем поле адреса
        checkoutPage.cityLocator.sendKeys(textCity); // заполняем поле города
        checkoutPage.stateLocator.sendKeys(textState); // заполняем поле области
        checkoutPage.postcodeLocator.sendKeys(textPostcode); // заполняем поле индекса
        checkoutPage.phoneLocator.sendKeys(textPhone); // заполняем поле телефона
        checkoutPage.paymentMethodeTransferLocator.click(); // выбираем способ оплаты "Оплата при доставке"
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButtonLocator));
        checkoutPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" на странице оформления заказа

        String textCheckingEmail = checkoutPage.checkingEmailLocator.getText(); // получаем почту из подтверждения

        String textCheckingAmount = checkoutPage.checkingAmountLocator.getText(); // запоминаем сумму к оплате из подтверждения
        textCheckingAmount = textCheckingAmount.replace(",", ".").replace("₽", ""); // меняем запятую на точку, убираем знак рубля
        BigDecimal numberCheckingAmount = new BigDecimal(textCheckingAmount); // переводим в формат BigDecimal

        String textCheckingPaymentMethod = checkoutPage.checkingPaymentMethodLocator.getText().toLowerCase(); // получаем метод оплаты из подтверждения
        String textApprove = checkoutPage.approveLocator.getText(); // получаем заголовок из подтверждения

        // assert
        Assertions.assertAll(
                // проверка заголовка:
                () -> Assertions.assertEquals("Заказ получен", textApprove, "Заказ не получен"),
                // проверка почты:
                () -> Assertions.assertEquals(textEmail, textCheckingEmail, "Мейл не совпадает"),
                // проверка суммы к оплате:
                () -> Assertions.assertEquals(numberAmountForPayment, numberCheckingAmount, "Сумма к оплате не совпадает"),
                // проверка метода оплаты:
                () -> Assertions.assertEquals("прямой банковский перевод", textCheckingPaymentMethod, "Сумма к оплате не совпадает")
        );
    }

    // 3. Добавить товар в корзину. Перейти к оформлению заказа. Заполнить обязательные поля. Применить купон. Выбрать оплату наличными при доставке. Проверить почту, сумму, метод оплаты, заголовок.
    @Test
    public void orderProductWithFilledParamsAndBankPaymentWithDiscount_DisplayedPositiveResultTest() {
        // arrange
        MainPage mainPage = new MainPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // act
        mainPage.searchFormLocator.sendKeys(product); // вводим названием продукта в окно поиска
        mainPage.searchFormButtonLocator.click(); // нажимаем кнопку поиска
        productPage.addToCartButtonLocator.click(); // нажимаем "В корзину"
        productPage.detailsLocator.click(); // нажимаем подробнее для перехода в корзину

        String textTotalAmount = cartPage.totalAmountLocator.getText(); // запоминаем общую стоимость товара
        textTotalAmount = textTotalAmount.replace(",", ".").replace("₽", ""); // меняем запятую на точку, убираем знак рубля
        BigDecimal numberTotalAmount = new BigDecimal(textTotalAmount); // переводим в формат BigDecimal

        cartPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" в корзине
        checkoutPage.pressToEnterCouponLocator.click(); // нажимаем для применения купона
        checkoutPage.couponInputLocator.sendKeys(textDiscount); // вводим купон
        checkoutPage.couponButtonLocator.click(); // нажимаем "Применить купон"
        checkoutPage.firstNameLocator.sendKeys(textFirstName); // заполняем поле имени
        checkoutPage.lastNameLocator.sendKeys(textLastName); // заполняем поле фамилии
        checkoutPage.addressLocator.sendKeys(textAddress); // заполняем поле адреса
        checkoutPage.cityLocator.sendKeys(textCity); // заполняем поле города
        checkoutPage.stateLocator.sendKeys(textState); // заполняем поле области
        checkoutPage.postcodeLocator.sendKeys(textPostcode); // заполняем поле индекса
        checkoutPage.phoneLocator.sendKeys(textPhone); // заполняем поле телефона
        checkoutPage.paymentMethodeUponDeliveryLocator.click(); // выбираем способ оплаты "Оплата при доставке"
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButtonLocator));
        checkoutPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" на странице оформления заказа
        String textCheckingEmail = checkoutPage.checkingEmailLocator.getText(); // получаем почту из подтверждения

        String textCheckingAmount = checkoutPage.checkingAmountLocator.getText(); // запоминаем сумму к оплате из подтверждения
        textCheckingAmount = textCheckingAmount.replace(",", ".").replace("₽", ""); // меняем запятую на точку, убираем знак рубля
        BigDecimal numberCheckingAmount = new BigDecimal(textCheckingAmount); // переводим в формат BigDecimal

        String textCheckingPaymentMethod = checkoutPage.checkingPaymentMethodLocator.getText().toLowerCase(); // получаем метод оплаты из подтверждения
        String textApprove = checkoutPage.approveLocator.getText(); // получаем заголовок из подтверждения

        // assert
        Assertions.assertAll(
                // проверка заголовка:
                () -> Assertions.assertEquals("Заказ получен", textApprove, "Заказ не получен"),
                // проверка почты:
                () -> Assertions.assertEquals(textEmail, textCheckingEmail, "Мейл не совпадает"),
                // проверка суммы к оплате:
                () -> Assertions.assertEquals(numberTotalAmount.subtract(numberDiscount), numberCheckingAmount, "Сумма к оплате не совпадает"),
                // проверка метода оплаты:
                () -> Assertions.assertEquals("оплата при доставке", textCheckingPaymentMethod, "Сумма к оплате не совпадает")
        );
    }

        // 4. Добавить товар в корзину. Перейти к оформлению заказа. Не заполнять обязательные поля. Проверить отображение ошибки.
    @Test
    public void orderProductWithNotFilledParams_displayedErrorsTest() {
        // arrange
        MainPage mainPage = new MainPage(driver);
        ProductPage productPage = new ProductPage(driver);
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // act
        mainPage.searchFormLocator.sendKeys(product); // вводим названием продукта в окно поиска
        mainPage.searchFormButtonLocator.click(); // нажимаем кнопку поиска
        productPage.addToCartButtonLocator.click(); // нажимаем "В корзину"
        productPage.detailsLocator.click(); // нажимаем подробнее для перехода в корзину
        cartPage.checkoutButtonLocator.click(); // нажимаем кнопку "Оформить заказ" в корзине
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        wait.until(ExpectedConditions.elementToBeClickable(checkoutPage.checkoutButtonLocator));
        checkoutPage.checkoutButtonLocator.click();// нажимаем кнопку "Оформить заказ" на странице оформления заказа
        driver.findElement(By.cssSelector("Имя для выставления счета"));
        driver.findElement(By.cssSelector("Фамилия для выставления счета"));
        driver.findElement(By.cssSelector("Город / Населенный пункт для выставления счета"));
        driver.findElement(By.cssSelector("Область для выставления счета"));
        driver.findElement(By.cssSelector("Почтовый индекс для выставления счета"));
        driver.findElement(By.cssSelector("Адрес для выставления счета"));
        driver.findElement(By.cssSelector("Телефон для выставления счета"));
        driver.findElement(By.cssSelector("Адрес почты для выставления счета"));

        // assert
        Assertions.assertAll(
                () -> Assertions.assertTrue(checkoutPage.errorLocator.isDisplayed(), "Не отобразилось предупреждение об обязательных для заполнения полях")
        );
    }
}
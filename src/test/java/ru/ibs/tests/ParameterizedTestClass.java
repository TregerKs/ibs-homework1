package ru.ibs.tests;

import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class ParameterizedTestClass {
    static WebDriver driver;
    static WebDriverWait wait;

    @Parameterized.Parameters
    public static Collection<Object> data() {
        return Arrays.asList(new Object[][]{
                {"Коля Коля Николай", " (936) 654-6555"},
                {"Ыгорь", " (900) 000-6505"},
                {"Степанида Аркапр", " (111) 654-6115"}
        });
    }

    @Parameterized.Parameter(0)
    public String name;

    @Parameterized.Parameter(1)
    public String phone;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10, 1000);
        driver.manage().window().maximize();
    }

    @Test
    public void test() throws InterruptedException {

        driver.get("https://www.rgs.ru");
        WebElement company = driver.findElement(By.xpath("//*[contains(text(), \"Компаниям\") and @href]"));
        waitElementToBeClickable(company);
        company.click();

        wait.until(ExpectedConditions.attributeContains(company, "Class", "nuxt-link-exact-active"));

       try {
        WebElement health = driver.findElement(By.xpath("//*[contains(text(), \"Здоровье\") and @class=\"padding\"]"));
        waitElementToBeClickable(health);
        health.click();
       } catch (ElementClickInterceptedException e) {
           Thread.sleep(3000);
           WebElement closeBannerButton = driver.findElement(By.xpath("//div[@class=\"widget__close js-collapse-login\"]"));
           waitElementToBeClickable(closeBannerButton);
           closeBannerButton.click();
       }

        WebElement insurance = driver.findElement(By.xpath("//*[contains(text(), \"Добровольное медицинское страхование\") and @href]"));
        waitElementToBeClickable(insurance);
        insurance.click();
        Thread.sleep(3000);

        WebElement checkText = driver.findElement(By.xpath("//h1[@data-v-1b4d1132]"));
        Assert.assertTrue("Страничка не загрузилась", checkText.isDisplayed());
        Assert.assertEquals("Заголовок не совпал с ожидаемым", "Добровольное медицинское страхование", checkText.getText());

        WebElement application = driver.findElement(By.xpath("//*[button/span[contains(text(), \"Отправить заявку\")]]"));
        waitElementToBeClickable(application);
        application.click();

        checkText = driver.findElement(By.xpath("//h2[text()=\"Оперативно перезвоним\"]"));
        scrollToElementJs(checkText);

        Assert.assertTrue("Страничка не загрузилась", checkText.isDisplayed());
        Assert.assertEquals("Заголовок не совпал с ожидаемым", "Оперативно перезвоним\n" +
                "для оформления полиса", checkText.getText());

        String fieldXPath = "//input[@name=\"%s\"]";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), name);
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), phone);
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        WebElement checkAgree = driver.findElement(By.xpath("//div[@class=\"checkbox-body form__checkbox\"]/input"));

        WebElement userNameInput = driver.findElement(By.xpath(String.format(fieldXPath, "userName")));
        MatcherAssert.assertThat("Ну чото не так пошло((((99", userNameInput.getAttribute("value"),
                allOf(containsString("Коля"), endsWith("Ыгорь"), startsWith("Степанида")));

        WebElement addressField = driver.findElement(By.xpath("//div[@class=\"vue-dadata__search\"]/input"));
        fillInputField(addressField, "г Москва");

        Actions actions = new Actions(driver);
        actions.moveToElement(checkAgree).click(checkAgree).build().perform();

        WebElement contactMeButton = driver.findElement(By.xpath("//button[@class=\"form__button-submit btn--basic\" and contains(text(), \"Свяжитесь со мной\")]"));
        waitElementToBeClickable(contactMeButton);
        contactMeButton.click();
        Thread.sleep(3000);

        WebElement email = driver.findElement(By.xpath(String.format(fieldXPath, "userEmail")));
        checkCorrect(email);
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }

    @AfterClass
    public static void afterClass() {
        driver.quit();
    }

    private void checkCorrect(WebElement element) {
        WebElement errorInfo = element.findElement(By.xpath("./../../span[@class=\"input__error text--small\"]"));
        wait.until(ExpectedConditions.textToBePresentInElement(errorInfo, "Введите корректный адрес электронной почты"));
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void waitElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
}

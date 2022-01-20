package ru.ibs.tests;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium {
   static WebDriver driver;
    static WebDriverWait wait;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10, 1000);
        driver.manage().window().maximize();
    }



    @Test
    public void test() throws InterruptedException {

        //неявное ожидание
//        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //явное ожидание wait = new WebDriverWait(driver, 10, 1000);


//        driver.get("https://www.sberbank.ru/ru/person");
////         driver.navigate().to("https://www.sberbank.ru/ru/person"); //сохраняет историю переходов в браузере
////        driver.navigate().back(); //вернуться назад
//        WebElement baseMenu = driver.findElement(By.xpath("//*[contains(text(), 'Страхование') and @role]"));
//        baseMenu.click();
//
//        driver.quit();

        // WebDriverWait wait = new WebDriverWait(driver, 10, 1000);


        driver.get("https://www.rgs.ru");
        WebElement company = driver.findElement(By.xpath("//*[contains(text(), \"Компаниям\") and @href]"));
        waitElementToBeClickable(company);
        company.click();

        wait.until(ExpectedConditions.attributeContains(company, "Class", "nuxt-link-exact-active"));

        WebElement health = driver.findElement(By.xpath("//*[contains(text(), \"Здоровье\") and @class=\"padding\"]"));
        waitElementToBeClickable(health);
        //Thread.sleep(3000);
        health.click();


        //driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        WebElement insurance = driver.findElement(By.xpath("//*[contains(text(), \"Добровольное медицинское страхование\") and @href]"));
        waitElementToBeClickable(insurance);
        insurance.click();
        Thread.sleep(3000);
        // wait.until(ExpectedConditions.elementToBeClickable(baseMenu)); (3.07)

//        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//h1[@data-v-1b4d1132]"))));
//        WebElement parentInsurance = insurance.findElement(By.xpath("./.."));
//        wait.until(ExpectedConditions.attributeContains(parentInsurance, "Class", "active"));

        //WebElement checkText = driver.findElement(By.xpath("//h1[@data-v-1b4d1132]"));
        //wait.until(ExpectedConditions.);
        //Thread.sleep(3000);
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
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), "Николай Иванович Бах");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), " (995) 123-4567");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        WebElement checkAgree = driver.findElement(By.xpath("//div[@class=\"checkbox-body form__checkbox\"]/input"));
        // scrollToElementJs(checkAgree);

        WebElement addressField = driver.findElement(By.xpath("//div[@class=\"vue-dadata__search\"]/input"));
        fillInputField(addressField, "г Москва");

        Actions actions = new Actions(driver);
        actions.moveToElement(checkAgree).click(checkAgree).build().perform();

        WebElement contactMeButton = driver.findElement(By.xpath("//button[@class=\"form__button-submit btn--basic\" and contains(text(), \"Свяжитесь со мной\")]"));
        waitElementToBeClickable(contactMeButton);
        contactMeButton.click();
        Thread.sleep(3000);

        // Проверка на ошибку в поле email:
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

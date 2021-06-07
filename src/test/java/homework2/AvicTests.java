package homework2;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.openqa.selenium.Keys.ENTER;


public class AvicTests {

    WebDriver driver;

    @BeforeTest
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get("https://avic.ua/ua");
    }

    @Test
    public void checkIfAscendingSortWorking() {
        Actions actions = new Actions(driver);
        WebDriverWait wait = new WebDriverWait (driver, 10);

        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys("apple watch", ENTER);
        driver.findElement(By.xpath("//label[@for='fltr-1']")).click();
        driver.findElement(By.xpath("//div[@class='sort-holder'][.//label]//span[@dir]")).click();
        WebElement webElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(@id, 'priceasc')]")));
        actions.moveToElement(webElement).click().build().perform();
        driver.findElement(By.xpath("//a[@class='btn-see-more js_show_more']")).click();
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='prod-cart__prise-new']"));
        boolean check = true;
        int prevElement = 0;
        for (WebElement element : elements) {
            int elementPrice = Integer.parseInt(element.getText().substring(0, element.getText().indexOf(" ")));
            if (elementPrice >= prevElement) prevElement = elementPrice;
            else {
                check = false;
                break;
            }
        }
        Assert.assertTrue(check);
    }

    @Test
    public void checkThatSumInBasketCalculatesRight() {
        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys("apple watch", ENTER);
        driver.findElement(By.xpath("(//a[@class='prod-cart__buy'])[1]")).click();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));
        driver.findElement(By.xpath("//div[@id='js_cart']//a[@data-cart-add]")).click();
        String totalPriceText = driver.findElement(By.xpath("//div[@class='item-total']//span[@class='prise']")).getText();
        int totalPrice = Integer.parseInt(totalPriceText.substring(0, totalPriceText.indexOf(" ")));
        List<WebElement> itemPrice = driver.findElements(By.xpath("//div[@class='total-h']//span[@class='prise']"));
        int itemPriceSum = 0;
        for (WebElement element : itemPrice) {
            itemPriceSum += Integer.parseInt(element.getText().substring(0, element.getText().indexOf(" ")));
        }
        Assert.assertEquals(itemPriceSum, totalPrice);
    }

    @Test
    public void checkIfSumFiltersItemsInRange10000To20000() throws InterruptedException {
        Actions actions = new Actions(driver);
        WebElement menuItem1Lvl = driver.findElement(By.xpath("//a[descendant::span[text()='Ноутбуки та планшети']]"));
        WebElement menuItem2Lvl = driver.findElement(By.xpath("//div[contains(@class,'menu-lvl second-level')]//a[text()='Ноутбуки']"));
        actions.moveToElement(menuItem1Lvl).moveByOffset(10, 0).moveToElement(menuItem2Lvl).build().perform();
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(By.xpath("//a[text()='Ноутбуки']//parent::li//a[.//img[@alt='HP']]")).click();
        driver.findElement(By.xpath("//label[@for='fltr-1']")).click();
        driver.findElement(By.xpath("//input[@class='form-control form-control-min']")).clear();
        driver.findElement(By.xpath("//input[@class='form-control form-control-min']")).sendKeys("10000");
        driver.findElement(By.xpath("//input[@class='form-control form-control-max']")).clear();
        driver.findElement(By.xpath("//input[@class='form-control form-control-max']")).sendKeys("20000");
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//div[contains(@class, 'form')]//a")).click();
        List<WebElement> elementList = driver.findElements(By.xpath("//div[@class='prod-cart__prise-new']"));
        boolean check = true;
        for (WebElement element : elementList) {
            String price = element.getText().substring(0, element.getText().indexOf(" "));
            if (Integer.parseInt(price) < 10000 || Integer.parseInt(price) > 20000) check = false;
        }
        Assert.assertTrue(check);
    }

    @AfterMethod
    public void tearDown() {
    }
}
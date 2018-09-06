package com.xlm.demo.test;

import com.xlm.demo.model.DemoQAModel;
import com.xlm.demo.reporting.Extentx;
import com.xlm.demo.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Extentx.class)
public class ApplicationTests {

    public static Long testStartTime;
    public static Long testEndTime;
    DemoQAModel model = new DemoQAModel();

    @BeforeSuite
    public void beforeSuite() throws Exception {
        System.setProperty("webdriver.gecko.driver", "./webdriver/geckodriverv0.19.1/geckodriver.exe");
        Utility.loadProperty();
    }

    public WebDriver loadDriver(String browser) {
        WebDriver driver = null;

        if (browser.equalsIgnoreCase("Firefox")) {
            driver = new FirefoxDriver();
            driver.get(model.URL);
        } else if (browser.equalsIgnoreCase("Chrome")) {
            driver = new ChromeDriver();
            driver.get(model.URL);
        }

        return driver;
    }

    @Test(dataProvider = "TestButtons")
    public void applicationTest(By webElement, String expected) {

        testStartTime = System.currentTimeMillis();
        WebDriver driver = loadDriver("Firefox");

        driver.findElement(webElement).click();
        Assert.assertEquals(driver.findElement(model.landingPageHeader).getText(), expected);

        driver.quit();
        testEndTime = System.currentTimeMillis();
    }


    @DataProvider(name = "TestButtons"/*, parallel = true*/)
    public Object[][] dataprovider() {
        Object[][] testdata = new Object[5][2];

        testdata[0][0] = model.btn_draggable;
        testdata[0][1] = "Draggable";

        testdata[1][0] = model.btn_droppable;
        testdata[1][1] = "Droppable";

        testdata[2][0] = model.btn_resizable;
        testdata[2][1] = "Resizable";

        testdata[3][0] = model.btn_selectable;
        testdata[3][1] = "Selectable";

        testdata[4][0] = model.btn_sortable;
        testdata[4][1] = "Sortable";

        return testdata;
    }
}

package com.xlm.demo.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.xlm.demo.model.DemoQAModel;
import com.xlm.demo.reporting.Extentx;
import com.xlm.demo.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Listeners(Extentx.class)
public class ApplicationTests {

    public static Long testStartTime;
    public static Long testEndTime;
    DemoQAModel model = new DemoQAModel();
    private ExtentReports extent;

    public static String CaptureScreen(WebDriver driver, String ImagesPath) throws AWTException, IOException {
        Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenShot, "JPG", new File(ImagesPath + ".jpg"));
        return ImagesPath + ".jpg";
    }

    @BeforeSuite
    public void beforeSuite() throws Exception {
        System.setProperty("webdriver.gecko.driver", "./webdriver/geckodriverv0.19.1/geckodriver.exe");
        Utility.loadProperty();

        ExtentHtmlReporter htmlReporter = Utility.newExtentHtmlReporter();
        KlovReporter klov = Utility.newKlovReporter();
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        if (Utility.property.getProperty("UpdateReports").equalsIgnoreCase("yes")) {
            extent.attachReporter(klov);
        }

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
    public void applicationTest(HashMap<String, Object> testInputs) throws IOException, AWTException {

        testStartTime = System.currentTimeMillis();

        WebDriver driver = loadDriver("Firefox");

        driver.findElement((By) testInputs.get("WebElement")).click();
        Assert.assertEquals(driver.findElement(model.landingPageHeader).getText(), testInputs.get("Expected"));

        testInputs.put("screenShotPath", CaptureScreen(driver, "./screenshots/" + testInputs.get("Expected")));

        driver.quit();
        testEndTime = System.currentTimeMillis();
    }

    @AfterMethod
    public void postResults(ITestResult result) throws IOException {
        Map<String, String> parameters = (Map<String, String>) result.getParameters()[0];

        ExtentTest test = extent.createTest(String.valueOf(parameters.get("Expected")));


        if (result.getStatus() == ITestResult.FAILURE) {
            if (result.getThrowable() != null) {
                test.log(Status.FAIL, result.getThrowable() + "\n\n");
            } else {
                test.log(Status.FAIL, "Test " + Status.FAIL.toString().toLowerCase() + "ed \n" );
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            if (result.getThrowable() != null) {
                test.log(Status.PASS, result.getThrowable() + "\n\n");
            } else {
                test.log(Status.PASS, "Test " + Status.PASS.toString().toLowerCase() + "ed \n");
            }
            //test.addScreenCaptureFromPath("./screenshots/" + parameters.get("Expected") + ".jpg");
        } else if (result.getStatus() == ITestResult.SKIP) {
            if (result.getThrowable() != null) {
                test.log(Status.SKIP, result.getThrowable() + "\n\n" );
            } else {
                test.log(Status.SKIP, "Test " + Status.SKIP.toString().toLowerCase() + "ed \n" );
            }
        }
        test.getModel().setStartTime(getTime(result.getStartMillis()));
        test.getModel().setEndTime(getTime(result.getEndMillis()));

        extent.flush();
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    @DataProvider(name = "TestButtons", parallel = true)
    public Object[][] dataprovider() {
        Object[][] testdata = new Object[5][1];

        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> testInputs = new HashMap<>();
            if (i == 0) {
                testInputs.put("WebElement", model.btn_draggable);
                testInputs.put("Expected", "Draggable");
            } else if (i == 1) {
                testInputs.put("WebElement", model.btn_droppable);
                testInputs.put("Expected", "Droppable1");
            } else if (i == 2) {
                testInputs.put("WebElement", model.btn_resizable);
                testInputs.put("Expected", "Resizable");
            } else if (i == 3) {
                testInputs.put("WebElement", model.btn_selectable);
                testInputs.put("Expected", "Selectable");
            } else {
                testInputs.put("WebElement", model.btn_sortable);
                testInputs.put("Expected", "Sortable");
            }
            testdata[i][0] = testInputs;
        }

        return testdata;
    }
}
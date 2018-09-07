package com.xlm.demo.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.xlm.demo.model.DemoQAModel;
import com.xlm.demo.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ApplicationTests {

    DemoQAModel model = new DemoQAModel();
    String dest;
    private ExtentReports extent;

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

        testInputs.put("testStartTime", System.currentTimeMillis());

        WebDriver driver = loadDriver("Firefox");

        driver.findElement((By) testInputs.get("WebElement")).click();

        Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenShot, "JPG", new File("./screenshots/" + testInputs.get("Expected") + ".jpg"));
        testInputs.put("screenShotPath", "./screenshots/" + testInputs.get("Expected") + ".jpg");


        String actualValue = driver.findElement(model.landingPageHeader).getText();
        testInputs.put("Actual", actualValue);
        Assert.assertEquals(actualValue, testInputs.get("Expected"));

        String landingPageContent = driver.findElement(model.landingPageContent).getText();
        String[] actualDescription = landingPageContent.split("\n");
        testInputs.put("actualDescription", actualDescription[0]);
        Assert.assertEquals(actualDescription[0], testInputs.get("description"));

        driver.quit();
        testInputs.put("testEndTime", System.currentTimeMillis());
    }

    @AfterMethod
    public void postResults(ITestResult result) {
        HashMap<String, Object> parameters = (HashMap<String, Object>) result.getParameters()[0];

        ExtentTest test = extent.createTest(String.valueOf(parameters.get("Expected")));


        if (result.getStatus() == ITestResult.FAILURE) {
            if (result.getThrowable() != null) {
                test.log(Status.FAIL, result.getThrowable() + "<br />");
            } else {
                test.log(Status.FAIL, "Test " + Status.FAIL.toString().toLowerCase() + "ed <bold><br />");
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            if (result.getThrowable() != null) {
                test.log(Status.PASS, result.getThrowable() + "<br />");
            } else {
                test.log(Status.PASS, "Test " + Status.PASS.toString().toLowerCase() + "ed <br />" +
                        "Actual : " + parameters.get("Actual") + " Expected : " + parameters.get("Expected") + "<br />" +
                        "Actual Description : " + parameters.get("actualDescription") + " Expected Description : " + parameters.get("description"));
            }
            try {
                test.addScreenCaptureFromPath((String) parameters.get("screenShotPath"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            if (result.getThrowable() != null) {
                test.log(Status.SKIP, result.getThrowable() + "<br />");
            } else {
                test.log(Status.SKIP, "Test " + Status.SKIP.toString().toLowerCase() + "ed <bold><br />");
            }
        }
        test.getModel().setStartTime(getTime((Long) parameters.get("testStartTime")));
        test.getModel().setEndTime(getTime((Long) parameters.get("testEndTime")));

        extent.flush();
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    @DataProvider(name = "TestButtons")
    public Object[][] dataprovider() {
        Object[][] testdata = new Object[5][1];

        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> testInputs = new HashMap();
            if (i == 0) {
                testInputs.put("WebElement", model.btn_draggable);
                testInputs.put("Expected", "Draggable");
                testInputs.put("description", "Allow elements to be moved using the mouse.");
            } else if (i == 1) {
                testInputs.put("WebElement", model.btn_droppable);
                testInputs.put("Expected", "Droppable");
                testInputs.put("description", "Create targets for draggable elements.");
            } else if (i == 2) {
                testInputs.put("WebElement", model.btn_resizable);
                testInputs.put("Expected", "Resizable");
                testInputs.put("description", "Change the size of an element using the mouse.");
            } else if (i == 3) {
                testInputs.put("WebElement", model.btn_selectable);
                testInputs.put("Expected", "Selectable");
                testInputs.put("description", "Use the mouse to select elements, individually or in a group.");
            } else {
                testInputs.put("WebElement", model.btn_sortable);
                testInputs.put("Expected", "Sortable");
                testInputs.put("description", "Reorder elements in a list or grid using the mouse.");
            }
            testdata[i][0] = testInputs;
        }

        return testdata;
    }
}
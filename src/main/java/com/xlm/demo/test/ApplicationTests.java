package com.xlm.demo.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.xlm.demo.controller.TestController;
import com.xlm.demo.model.DemoQAModel;
import com.xlm.demo.pdfreporting.PDFReporter;
import com.xlm.demo.pdfreporting.PDFTestReportModel;
import com.xlm.demo.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class ApplicationTests {

    public static List<String> testCasestoExecute;
    @Autowired
    TestController testController;
    private DemoQAModel model = new DemoQAModel();
    private Robot robot;
    private ExtentReports extent;
    private PDFReporter pdfReporter;
    private Document pdf;

    @BeforeSuite
    public void beforeSuite() throws Exception {
        System.setProperty("webdriver.gecko.driver", "./webdriver/geckodriverv0.19.1/geckodriver.exe");
        System.setProperty("java.awt.headless", "false");
        Utility.loadProperty();
        robot = new Robot();
        ExtentHtmlReporter htmlReporter = Utility.newExtentHtmlReporter();
        KlovReporter klov = Utility.newKlovReporter();
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        if (Utility.property.getProperty("UpdateReports").equalsIgnoreCase("yes")) {
            extent.attachReporter(klov);
        }
        pdfReporter = new PDFReporter();
        pdf = pdfReporter.PDFReporter();

    }

    private WebDriver loadDriver(String browser) {
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
    public void applicationTest(HashMap<String, Object> testInputs) throws IOException {

        testInputs.put("testStartTime", System.currentTimeMillis());

        WebDriver driver = loadDriver("Firefox");

        driver.findElement((By) testInputs.get("WebElement")).click();

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

        driver.close();
        testInputs.put("testEndTime", System.currentTimeMillis());
    }

    @AfterMethod
    public void postResults(ITestResult result) throws Exception {
        HashMap parameters = (HashMap) result.getParameters()[0];

        ExtentTest test = extent.createTest(String.valueOf(parameters.get("testName")));
        PDFTestReportModel pdftest = new PDFTestReportModel(String.valueOf(parameters.get("testName")));


        if (result.getStatus() == ITestResult.FAILURE) {
            if (result.getThrowable() != null) {
                test.log(Status.FAIL, result.getThrowable() + "<br />");
                pdftest.setTestResult("FAIL");
                pdftest.setTestDescriptions(Arrays.asList(result.getThrowable()));
            } else {
                test.log(Status.FAIL, "Test " + Status.FAIL.toString().toLowerCase() + "ed <bold><br />");
                pdftest.setTestResult("FAIL");
                pdftest.setTestDescriptions(Arrays.asList("Test " + Status.FAIL.toString().toLowerCase() + "ed"));
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            if (result.getThrowable() != null) {
                test.log(Status.PASS, result.getThrowable() + "<br />");
                pdftest.setTestResult("PASS");
                pdftest.setTestDescriptions(Arrays.asList(result.getThrowable()));
            } else {
                test.log(Status.PASS, "Test " + Status.PASS.toString().toLowerCase() + "ed <br />" +
                        "Actual : " + parameters.get("Actual") + " Expected : " + parameters.get("Expected") + "<br />" +
                        "Actual Description : " + parameters.get("actualDescription") + " Expected Description : " + parameters.get("description"));

                pdftest.setTestResult("PASS");
                pdftest.setTestDescriptions(Arrays.asList("Test " + Status.PASS.toString().toLowerCase() + "ed",
                        "Actual : " + parameters.get("Actual") + " Expected : " + parameters.get("Expected"),
                        "Actual Description : " + parameters.get("actualDescription") + " Expected Description : " + parameters.get("description")));
            }
            try {
                test.addScreenCaptureFromPath((String) parameters.get("screenShotPath"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            if (result.getThrowable() != null) {
                test.log(Status.SKIP, result.getThrowable() + "<br />");
                pdftest.setTestResult("PASS");
                pdftest.setTestDescriptions(Arrays.asList(result.getThrowable()));
            } else {
                test.log(Status.SKIP, "Test " + Status.SKIP.toString().toLowerCase() + "ed <bold><br />");
                pdftest.setTestResult("PASS");
                pdftest.setTestDescriptions(Arrays.asList("Test " + Status.PASS.toString().toLowerCase() + "ed",
                        "Actual : " + parameters.get("Actual") + " Expected : " + parameters.get("Expected"),
                        "Actual Description : " + parameters.get("actualDescription") + " Expected Description : " + parameters.get("description")));

            }
        }
        test.getModel().setStartTime(Utility.getTime((Long) parameters.get("testStartTime")));
        test.getModel().setEndTime(Utility.getTime((Long) parameters.get("testEndTime")));

        extent.flush();
        pdf.add(pdftest.setTestResultTable());
        pdf.add(new Paragraph("\n"));

    }

    @AfterSuite
    public void suiteTearDown() {
        pdf.close();
    }

    @DataProvider(name = "TestButtons")
    public Object[][] dataprovider() {
        Object[][] testdata = new Object[5][1];

        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> testInputs = new HashMap<>();
            if (i == 0) {
                testInputs.put("testName", "btn_draggable");
                testInputs.put("WebElement", model.btn_draggable);
                testInputs.put("Expected", "Draggable");
                testInputs.put("description", "Allow elements to be moved using the mouse.");
            } else if (i == 1) {
                testInputs.put("testName", "btn_droppable");
                testInputs.put("WebElement", model.btn_droppable);
                testInputs.put("Expected", "Droppable");
                testInputs.put("description", "Create targets for draggable elements.");
            } else if (i == 2) {
                testInputs.put("testName", "btn_resizable");
                testInputs.put("WebElement", model.btn_resizable);
                testInputs.put("Expected", "Resizable");
                testInputs.put("description", "Change the size of an element using the mouse.");
            } else if (i == 3) {
                testInputs.put("testName", "btn_selectable");
                testInputs.put("WebElement", model.btn_selectable);
                testInputs.put("Expected", "Selectable");
                testInputs.put("description", "Use the mouse to select elements, individually or in a group.");
            } else {
                testInputs.put("testName", "btn_sortable");
                testInputs.put("WebElement", model.btn_sortable);
                testInputs.put("Expected", "Sortable");
                testInputs.put("description", "Reorder elements in a list or grid using the mouse.");
            }


            /*if (testInputs.get("testName") != null &&
                    testCasestoExecute.contains(testInputs.get("testName"))) {*/
            testdata[i][0] = testInputs;
            /*}*/
        }

        return testdata;
    }
}
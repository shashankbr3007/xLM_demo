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
    final String URL = "http://demoqa.com/";
    @Autowired
    TestController testController;
    private DemoQAModel model;
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
            driver.get(URL);
        } else if (browser.equalsIgnoreCase("Chrome")) {
            driver = new ChromeDriver();
            driver.get(URL);
        }

        return driver;
    }

    @Test(dataProvider = "TestButtons")
    public void applicationTest(HashMap<String, Object> testInputs) throws IOException {

        testInputs.put("testStartTime", System.currentTimeMillis());

        WebDriver driver = loadDriver("Firefox");
        model = new DemoQAModel(driver);

        model.click_btn(model.getButtonElement((String) testInputs.get("testName")));

        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenShot, "JPG", new File("./screenshots/" + testInputs.get("testName") + ".jpg"));
        testInputs.put("screenShotPath", "./screenshots/" + testInputs.get("testName") + ".jpg");


        String actuallandingPageHeader = model.get_landingPageHeader();
        testInputs.put("actuallandingPageHeader", actuallandingPageHeader);
        Assert.assertEquals(actuallandingPageHeader, testInputs.get("expectedlandingPageHeader"));

        String landingPageContent = model.get_landingPageContent();
        String[] actuallandingPageContent = landingPageContent.split("\n");
        testInputs.put("actuallandingPageContent", actuallandingPageContent[0]);
        Assert.assertEquals(actuallandingPageContent[0], testInputs.get("expectedlandingPageContent"));

        driver.close();
        testInputs.put("testEndTime", System.currentTimeMillis());
    }

    @AfterMethod
    public void postResults(ITestResult result) throws Exception {
        HashMap parameters = (HashMap) result.getParameters()[0];

        ExtentTest test = extent.createTest(String.valueOf(parameters.get("testName")));
        PDFTestReportModel pdftest = new PDFTestReportModel(String.valueOf(parameters.get("testName")));

        if (result.getStatus() == ITestResult.FAILURE) {
            updateReports(result, test, pdftest, parameters, Status.FAIL);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            updateReports(result, test, pdftest, parameters, Status.PASS);
        } else if (result.getStatus() == ITestResult.SKIP) {
            updateReports(result, test, pdftest, parameters, Status.SKIP);
        }

        if (parameters.get("screenShotPath") != null)
            test.addScreenCaptureFromPath((String) parameters.get("screenShotPath"));

        test.getModel().setStartTime(Utility.getTime((Long) parameters.get("testStartTime")));
        test.getModel().setEndTime(Utility.getTime((Long) parameters.get("testEndTime")));

        extent.flush();
        pdf.add(pdftest.setTestResultTable());
        pdf.add(new Paragraph("\n"));

    }

    private void updateReports(ITestResult result, ExtentTest test, PDFTestReportModel pdftest, HashMap parameters, Status status) {
        if (result.getThrowable() != null) {
            test.log(status, result.getThrowable() + "<br />");
            pdftest.setTestResult(status);
            pdftest.setTestDescriptions(Arrays.asList(result.getThrowable()));
        } else {
            test.log(status, "Test " + status.toString().toLowerCase() + "ed <br />" +
                    "Actual : " + parameters.get("actuallandingPageHeader") + " Expected : " + parameters.get("expectedlandingPageHeader") + "<br />" +
                    "Actual Description : " + parameters.get("actuallandingPageContent") + " Expected Description : " + parameters.get("expectedlandingPageContent"));

            pdftest.setTestResult(status);
            pdftest.setTestDescriptions(Arrays.asList("Test " + status.toString().toLowerCase() + "ed",
                    "Actual : " + parameters.get("actuallandingPageHeader") + " Expected : " + parameters.get("expectedlandingPageHeader"),
                    "Actual Description : " + parameters.get("actuallandingPageContent") + " Expected Description : " + parameters.get("expectedlandingPageContent")));
        }
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
                testInputs.put("expectedlandingPageHeader", "Draggable");
                testInputs.put("expectedlandingPageContent", "Allow elements to be moved using the mouse.");
            } else if (i == 1) {
                testInputs.put("testName", "btn_droppable");
                testInputs.put("expectedlandingPageHeader", "Droppable");
                testInputs.put("expectedlandingPageContent", "Create targets for draggable elements.");
            } else if (i == 2) {
                testInputs.put("testName", "btn_resizable");
                testInputs.put("expectedlandingPageHeader", "Resizable");
                testInputs.put("expectedlandingPageContent", "Change the size of an element using the mouse.");
            } else if (i == 3) {
                testInputs.put("testName", "btn_selectable");
                testInputs.put("expectedlandingPageHeader", "Selectable");
                testInputs.put("expectedlandingPageContent", "Use the mouse to select elements, individually or in a group.");
            } else {
                testInputs.put("testName", "btn_sortable");
                testInputs.put("expectedlandingPageHeader", "Sortable");
                testInputs.put("expectedlandingPageContent", "Reorder elements in a list or grid using the mouse.");
            }


           /* if (testInputs.get("testName") != null &&
                    testCasestoExecute.contains(testInputs.get("testName"))) {*/
            testdata[i][0] = testInputs;
            /*}*/
        }

        return testdata;
    }
}
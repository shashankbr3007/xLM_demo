package com.xlm.demo.reporting;


import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.xlm.demo.utility.Utility;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Extentx implements IReporter {

    private static final String FILE_NAME = "./Reports/ExtentReport_3.5.html";

    private ExtentReports extent;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        init();

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();

                try {
                    buildTestNodes(context.getFailedTests(), Status.FAIL);
                    buildTestNodes(context.getSkippedTests(), Status.SKIP);
                    buildTestNodes(context.getPassedTests(), Status.PASS);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        for (String s : Reporter.getOutput()) {
            extent.setTestRunnerOutput(s);
        }

        extent.flush();
    }

    private void init() {

        Properties property = Utility.property;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mmz");
        String UpdateReports = property.getProperty("UpdateReports").trim();
        String ProjectName = property.getProperty("project_name").trim();
        String ReportName = "Demo Automation Report ";
        String ServerUrl = property.getProperty("extentx_url").trim();
        String html_DocumentTitle = property.getProperty("html_DocumentTitle").trim();
        String html_ReportName = ReportName;

        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(FILE_NAME);
        htmlReporter.config().setDocumentTitle(html_DocumentTitle);
        htmlReporter.config().setReportName(html_ReportName);
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setChartVisibilityOnOpen(true);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setAnalysisStrategy(AnalysisStrategy.TEST);
        extent.setAnalysisStrategy(AnalysisStrategy.SUITE);
        extent.setAnalysisStrategy(AnalysisStrategy.CLASS);
        extent.setReportUsesManualConfiguration(true);


        KlovReporter klov = new KlovReporter();
        klov.setProjectName(ProjectName);
        klov.setReportName(ReportName);
        klov.setStartTime(new Date(System.currentTimeMillis()));
        klov.setEndTime(new Date(System.currentTimeMillis()));
        klov.setKlovUrl("http://localhost");  // url where Klov is running
        klov.initMongoDbConnection("localhost", 27017);
        klov.setDbName("klov");


        if (UpdateReports != null) {
            if (UpdateReports.equalsIgnoreCase("true") || UpdateReports.equalsIgnoreCase("yes")) {
                extent.attachReporter(klov);
                System.out.println("Extent report has been uploaded to Extentx server. ashboard can be accessed by url: " + ServerUrl);
            } else {
                System.out.println("UpdateReports to Extentx server is disabled: To enable please set property UpdateReports   with value (yes/true) in [Filename].properties properties file in EMMProperties folder");
            }
        } else {
            System.out.println("UpdateReports to Extentx server property details not provided in  properties file");
        }
    }

    private void buildTestNodes(IResultMap tests, Status status) throws IOException {
        ExtentTest test;


        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {

                HashMap<String, Object> parameters = (HashMap<String, Object>) result.getParameters()[0];
                test = extent.createTest(String.valueOf(parameters.get("Expected")));


                for (String group : result.getMethod().getGroups())
                    test.assignCategory(group);

                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable() + "\n\n");
                } else {
                    test.log(status, "Test " + status.toString().toLowerCase() + "ed \n");
                    //test.addScreenCaptureFromPath(String.valueOf(parameters.get("screenShotPath")));
                }

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));
            }
        }
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }


}

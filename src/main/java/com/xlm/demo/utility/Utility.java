package com.xlm.demo.utility;

import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class Utility {

    public static Properties property = new Properties();

    public static void loadProperty() throws Exception {

        try {
            property.load(new FileInputStream(new File("./src/main/resources/application.properties")));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    public static ExtentHtmlReporter newExtentHtmlReporter() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("Reports/ExtentReport_3.5.html");
        htmlReporter.config().setDocumentTitle(property.getProperty("html_DocumentTitle"));
        htmlReporter.config().setReportName(property.getProperty("html_ReportName"));
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setChartVisibilityOnOpen(true);

        return htmlReporter;
    }

    public static KlovReporter newKlovReporter() {
        String ProjectName = property.getProperty("project_name").trim();
        String ReportName = "Demo Automation Report ";

        KlovReporter klov = new KlovReporter();
        klov.setProjectName(ProjectName);
        klov.setReportName(ReportName);
        klov.setStartTime(new Date(System.currentTimeMillis()));
        klov.setEndTime(new Date(System.currentTimeMillis()));
        klov.setKlovUrl("http://localhost:8181");  // url where Klov is running
        klov.initMongoDbConnection("localhost", 27017);
        klov.setDbName("klov");
        return klov;
    }

    public static Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}

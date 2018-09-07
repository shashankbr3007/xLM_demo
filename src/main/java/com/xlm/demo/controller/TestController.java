package com.xlm.demo.controller;

import com.xlm.demo.test.ApplicationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.testng.TestNG;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TestController {

    @Autowired
    ApplicationTests tests;


    @RequestMapping(value = "/trigger", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public Map triggerTests(String[] testCases) {

        new Thread(() -> {
            ApplicationTests.testCasestoExecute = Arrays.asList(testCases);
            TestNG testNG = new TestNG();
            testNG.setTestClasses(new Class[]{com.xlm.demo.test.ApplicationTests.class});
            testNG.run();
        }).start();

        Map<String, String> map = new HashMap<>();
        map.put("Message", "Execution Started, please check \"http://localhost:8181/dashboard\" for latest Reports");
        map.put("Test Cases being executed", String.valueOf(testCases.length));
        map.put("Date", String.valueOf(System.currentTimeMillis()));
        return map;
    }
}

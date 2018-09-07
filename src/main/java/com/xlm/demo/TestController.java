package com.xlm.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.testng.TestNG;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TestController {


    @RequestMapping(value = "/trigger", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public Map triggerTests(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("Message", message);
        map.put("Date", String.valueOf(System.currentTimeMillis()));

        //new Thread(() -> {
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[]{com.xlm.demo.test.ApplicationTests.class});
        testNG.run();
        //}).start();

        return map;
    }
}

package com.xlm.demo.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utility {

    public static Properties property = new Properties();

    public static void loadProperty() throws Exception {

        try {
            property.load(new FileInputStream(new File("application.properties")));
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}

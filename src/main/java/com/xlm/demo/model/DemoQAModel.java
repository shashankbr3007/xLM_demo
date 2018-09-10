package com.xlm.demo.model;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class DemoQAModel {

    WebDriver driver;
    By btn_draggable = By.id("menu-item-140");
    By btn_droppable = By.id("menu-item-141");
    By btn_resizable = By.id("menu-item-143");
    By btn_selectable = By.id("menu-item-142");
    By btn_sortable = By.id("menu-item-151");
    By landingPageHeader = By.className("entry-title");
    By landingPageContent = By.className("entry-content");
    By homePageImage = By.className("site-anchor");


    public DemoQAModel(WebDriver driver) {
        this.driver = driver;
    }

    public void click_btn(By Button) {
        driver.findElement(Button).click();
    }

    public By getButtonElement(String buttonName) {

        switch (buttonName) {
            case "btn_draggable":
                return btn_draggable;

            case "btn_droppable":
                return btn_droppable;

            case "btn_resizable":
                return btn_resizable;

            case "btn_selectable":
                return btn_selectable;

            case "btn_sortable":
                return btn_sortable;

            default:
                return null;
        }
    }

    public String get_landingPageHeader() {
        return driver.findElement(landingPageHeader).getText();
    }

    public String get_landingPageContent() {
        return driver.findElement(landingPageContent).getText();
    }

}

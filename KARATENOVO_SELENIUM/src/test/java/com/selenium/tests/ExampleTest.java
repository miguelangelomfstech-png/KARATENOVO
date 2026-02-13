package com.selenium.tests;

import com.selenium.framework.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExampleTest extends BaseTest {

    @Test
    public void verifyGoogleTitle() {
        driver.get("https://www.google.com");
        String title = driver.getTitle();
        System.out.println("Page title is: " + title);
        Assertions.assertTrue(title.contains("Google"), "Title should contain 'Google'");
    }
}

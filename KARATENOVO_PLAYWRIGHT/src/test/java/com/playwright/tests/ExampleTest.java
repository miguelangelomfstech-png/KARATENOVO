package com.playwright.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.playwright.framework.BaseTest;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ExampleTest extends BaseTest {

    @Test
    public void verifyPlaywrightSetup() {
        // Navigate to a sample page
        page.navigate("https://playwright.dev");

        // Expect a title "to contain" a substring.
        assertThat(page).hasTitle("Fast and reliable end-to-end testing for modern web apps | Playwright");

        // create a locator
        Locator getStarted = page.locator("text=Get started");

        // Expect an attribute "to be strictly equal" to the value.
        assertThat(getStarted).hasAttribute("href", "/docs/intro");

        // Click the get started link.
        getStarted.click();

        // Expects the URL to contain intro.
        assertThat(page).hasURL("https://playwright.dev/docs/intro");
    }
}

using NUnit.Framework;
using OpenQA.Selenium;

namespace SeleniumTests
{
    public class ExampleTest : BaseTest
    {
        [Test]
        public void VerifyGoogleTitle()
        {
            driver.Navigate().GoToUrl("https://www.google.com");
            string title = driver.Title;
            System.Console.WriteLine("Page title is: " + title);
            Assert.That(title, Does.Contain("Google"), "Title should contain 'Google'");
        }
    }
}

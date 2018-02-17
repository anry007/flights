package scrapers.core;

import org.openqa.selenium.WebDriver;

import scrapers.core.wd.WebDriverFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	WebDriverFactory wdFactory = new WebDriverFactory();
    	WebDriver driver = wdFactory.getNewWebDriver();
    	try {
    		driver.get("http://google.com");
    	} finally {
    		driver.quit();
    	}
    }
}

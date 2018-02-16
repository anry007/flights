package scrapers.core;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	ChromeDriverService driverService = new ChromeDriverService.Builder()
    			.usingDriverExecutable(new File("./dependencies/chromedriver"))
    			.usingAnyFreePort()
    			.build();
    	WebDriver driver = new ChromeDriver(driverService);
    	try {
    		driver.get("http://google.com");
    	} finally {
    		driver.quit();
    	}
    }
}

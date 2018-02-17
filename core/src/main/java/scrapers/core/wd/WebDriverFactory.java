package scrapers.core.wd;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

public class WebDriverFactory {
	private final WebDriverPathProvider wdPathProvider;
	
	public WebDriverFactory() {
		wdPathProvider = WebDriverPathProvider.getProvider();
	}
	
	public WebDriver getNewWebDriver() {
		return new ChromeDriver(getDefaultDriverService());
	}
	
	private ChromeDriverService getDefaultDriverService() {
    	final File wdPathFile = new File(wdPathProvider.getWDPath());
		return new ChromeDriverService.Builder()
    			.usingDriverExecutable(wdPathFile)
    			.usingAnyFreePort()
    			.build();
	}
}

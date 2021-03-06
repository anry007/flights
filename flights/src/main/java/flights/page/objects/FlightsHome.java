package flights.page.objects;

import java.util.List;
import java.util.concurrent.Future;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/**
 * Base class defining flights home page object
 * @author Anry
 *
 */
public abstract class FlightsHome {
	protected final WebDriver driver;
	protected final String url;
	
	public FlightsHome(WebDriver driver, String url) {
		this.driver = driver;
		this.url = url;
	}
	
	/**
	 * Loads the page. Makes sure that provided page is unloaded first
	 * @param fromPage page to make sure is unloaded
	 */
	public void load(FlightsHome fromPage) {
		fromPage.awaitUnload();
		invokeUrl();
		fromPage.waitUnload();
		waitLoadComplete();
	}

	private void invokeUrl() {
		try {
			driver.get(url);
		} catch (TimeoutException e) {
			// we don't care because we managing loading ourselves
		}
	}
	
	/**
	 * Loads the page
	 */
	public void load() {
		invokeUrl();
		waitLoadComplete();
	}
	
	/**
	 * Returns list of prices on a given flights page
	 * @return
	 */
	public abstract List<Integer> getPrices();
	
	/**
	 * Waits for the page to load
	 */
	protected abstract void waitLoadComplete();
	
	/**
	 * Starts waiting for page to unload
	 * @return {@link Feature} to wait on for unload to complete
	 */
	protected abstract Future<Boolean> awaitUnload();
	
	/**
	 * Waits for unload to complete
	 */
	protected abstract void waitUnload();
}

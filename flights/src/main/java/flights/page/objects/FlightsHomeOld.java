package flights.page.objects;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

import scrapers.core.wd.WebDriverConstants;

/**
 * class representing old flights home page object
 * @author Anry
 *
 */
public class FlightsHomeOld extends FlightsHome {
	private Future<Boolean> unloadAwaiter;
	private List<WebElement> priceElements;
	private final By chooseOutboundFlightLocator = By.xpath("//div[contains(text(),'Sort by price')]"); 
	private final By noResultsFoundLocator = By.xpath("//div[contains(text(),'No results found matching your criteria.')]");
	private final By noFlightsLocator = By.xpath("//div[contains(text(),'Your search did not match any of our flights.')]");
	private final By priceContainer = By.xpath("//div[contains(text(),'Sort by price')]/../../../..");
	private final ExpectedCondition<Boolean> loadedCondition; 

	public FlightsHomeOld(WebDriver driver, String url) {
		super(driver, url);
		final ExpectedCondition<WebElement> condition1 = ExpectedConditions.visibilityOfElementLocated(chooseOutboundFlightLocator);
		final ExpectedCondition<WebElement> condition2 = ExpectedConditions.visibilityOfElementLocated(noResultsFoundLocator);
		final ExpectedCondition<WebElement> condition3 = ExpectedConditions.visibilityOfElementLocated(noFlightsLocator);
		loadedCondition = ExpectedConditions.or(condition1, condition2, condition3);
	}

	@Override
	public List<Integer> getPrices() {
		return priceElements.stream().map(e -> parsePrice(e.getText())).collect(Collectors.toList());
	}

	@Override
	protected void waitLoadComplete() {
		// wait for page to open
		boolean pageLoaded = false;
		int i = 0;
		while(!pageLoaded && i < 2) {
			getWebDriberWait().until(loadedCondition);
			
			List<WebElement> noResultsFound = driver.findElements(noResultsFoundLocator);
			if(noResultsFound.size() > 0 && noResultsFound.get(0).isDisplayed()) {
				priceElements = Collections.emptyList();
				pageLoaded = true;
			}
	
			List<WebElement> noFlightsFound = driver.findElements(noFlightsLocator);
			if(noFlightsFound.size() > 0 && noFlightsFound.get(0).isDisplayed()) {
				priceElements = Collections.emptyList();
				pageLoaded = true;
			}
	
			final List<WebElement> successElements = driver.findElements(chooseOutboundFlightLocator);
			if(successElements.size() > 0 && successElements.get(0).isDisplayed()) {
				waitForLoaderToDisappear(successElements.get(0));
				retrievePriceElements();
				pageLoaded = true;
			}
			i++;
		}
		if(!pageLoaded) {
			System.out.println("Page Not Loaded");
		}
	}

	/**
	 * Retrieves price elements from the page and stores them
	 */
	private void retrievePriceElements() {
		final WebElement pricesContainer = driver.findElement(priceContainer);
		priceElements = pricesContainer.findElements(By.xpath(".//div[starts-with(text(), '$')]"));
		priceElements = priceElements.stream().filter(e -> e.isDisplayed()).collect(Collectors.toList());
	}

    /**
     * Parses string representation of the price
     * @param priceStr
     * @return
     */
	private int parsePrice(String priceStr) {
    	priceStr = priceStr.split("–")[0]; // there might be range (e.g.: $297–$300)
    	int price = Integer.parseInt(priceStr.replace("$", "").replace(",", ""));
    	return price;
	}

	/**
	 * Waits for loader to disappear
	 * @param loaderElement
	 */
	private void waitForLoaderToDisappear(final WebElement loaderNeighbourElement) {
		final WebElement loaderElement = loaderNeighbourElement.findElement(By.xpath("../../../div/div"));
		getWebDriberWait().until(
				new Function<WebDriver, Boolean>() {
			        // save previous class
					private String lastCss = loaderElement.getAttribute("class");
			        // count number of call that happened since last class change 
					private int callsWithSameCss = 0;
					
			        public Boolean apply(WebDriver driver) {
			        	final String currentClass = loaderElement.getAttribute("class");
						if(lastCss.equals(currentClass)) {
							if(callsWithSameCss == 8) {
								return true;
							} else {
								callsWithSameCss++;
								return false;
							}
						} else {
							callsWithSameCss = 0;
							lastCss = currentClass;
							return false;
						}
			        }
			    });
	}

	@Override
	protected Future<Boolean> awaitUnload() {
        ExecutorService unloadExecutor = Executors.newSingleThreadExecutor();
        unloadAwaiter = unloadExecutor.submit(getAwaitUnloadCallable());
        return unloadAwaiter;
	}

    protected Callable<Boolean> getAwaitUnloadCallable(){
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
        		if(priceElements.size() > 0) {
        			getWebDriberWait()
        				.until(ExpectedConditions.stalenessOf(priceElements.get(0)));
        			return true;
        		} else {
        			return false;
        		}
            }
        };
    }

	private FluentWait<WebDriver> getWebDriberWait() {
		return new WebDriverWait(driver, WebDriverConstants.PAGE_LOAD_TIMEOUT_SECONDS).pollingEvery(1, TimeUnit.SECONDS);
	}
	
	@Override
	protected void waitUnload() {
        if(null == unloadAwaiter) {
            throw new UnsupportedOperationException("Page unload is not awaited. You should call awaitUnload() first");
        }

        try {
            // block until refresh is not started
            unloadAwaiter.get(WebDriverConstants.PAGE_LOAD_TIMEOUT_SECONDS + 2, TimeUnit.SECONDS);
        } catch (java.util.concurrent.TimeoutException x) {
            // we need to throw RunTimeException here, so wrapping it into Selenium implementation
            throw new TimeoutException("Page was not unloaded", x);
        } catch (Exception x) {
            throw new RuntimeException(x);
        } finally {
            // reset to null, so user need to call await again for next waitReload() call
            unloadAwaiter = null;
        }
	}
}

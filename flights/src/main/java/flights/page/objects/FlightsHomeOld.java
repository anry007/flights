package flights.page.objects;

import java.util.List;
import java.util.concurrent.Future;

import org.openqa.selenium.WebDriver;

/**
 * class representing old flights home page object
 * @author Anry
 *
 */
public class FlightsHomeOld extends FlightsHome {
	private Future<Boolean> unloadAwaiter;
	
	public FlightsHomeOld(WebDriver driver, String url) {
		super(driver, url);
	}

	@Override
	public List<Integer> getPrices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void waitLoadComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Future<Boolean> awaitUnload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void waitUnload() {
		// TODO Auto-generated method stub
		
	}
}

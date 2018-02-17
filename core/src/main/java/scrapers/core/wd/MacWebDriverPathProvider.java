package scrapers.core.wd;

public class MacWebDriverPathProvider extends WebDriverPathProvider {
	private static final String MAC_WD = "mac64_chromedriver";

	@Override
	protected String getPathInternal() {
		return MAC_WD;
	}

}

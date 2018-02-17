package scrapers.core.wd;

public class LinuxWebDriverPathProvider extends WebDriverPathProvider {
	private static final String LINUX_WD = "linux64_chromedriver";

	@Override
	protected String getPathInternal() {
		return LINUX_WD;
	}

}

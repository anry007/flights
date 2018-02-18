package scrapers.core.wd;

/**
 * Windows specific {@link WebDriverPathProvider}
 * @author Anry
 *
 */
public class WinWebDriverPathProvider extends WebDriverPathProvider {
	private static final String WIN_WD = "win32_chromedriver.exe";

	@Override
	protected String getPathInternal() {
		return WIN_WD;
	}

}

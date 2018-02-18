package scrapers.core.wd;

/**
 * Base class that provides platform specific path to Web Driver
 * @author Anry
 *
 */
public abstract class WebDriverPathProvider {
	protected static final String BASE_PATH = "webDrivers/2.35/";
	private static final String osName = System.getProperty("os.name").toLowerCase();;
	
	public String getWDPath() {
		final ClassLoader classLoader = getClass().getClassLoader();
		return classLoader.getResource(BASE_PATH + getPathInternal()).getFile();
	}

	protected abstract String getPathInternal();
	
	public static WebDriverPathProvider getProvider() {
		if(osName.indexOf("win") >= 0) {
			return new WinWebDriverPathProvider();
		}
		if(osName.indexOf("mac") >= 0) {
			return new MacWebDriverPathProvider();
		}
		if(osName.indexOf("nux") >= 0) {
			return new LinuxWebDriverPathProvider();
		}
		throw new UnsupportedOperationException("Environment " + osName + " is not suppoerted");	}

}

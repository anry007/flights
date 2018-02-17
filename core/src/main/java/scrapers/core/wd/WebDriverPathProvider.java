package scrapers.core.wd;

public abstract class WebDriverPathProvider {
	protected static final String BASE_PATH = "./src/main/resources/webDrivers/2.35/";
	private static final String osName = System.getProperty("os.name").toLowerCase();;
	
	public String getWDPath() {
		return BASE_PATH + getPathInternal();
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

package flights;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.common.collect.Lists;

import flights.page.objects.FlightsHome;
import flights.page.objects.FlightsHomeOld;
import flights.results.ResultsHolder;

public class Runner {
	public static final Date MIN_START_DATE = toDate("2018-06-14");
	public static final Date MAX_START_DATE = toDate("2018-07-23");
	public static final Date MAX_END_DATE = toDate("2018-08-23");
	public static final int MIN_DURATION = 30;
	public static final int MAX_DURATION = 55;
	
	private static final ResultsHolder results = new ResultsHolder();
	private static final BlockingDeque<Map.Entry<WebDriver, FlightsHome>> drivers = 
			new LinkedBlockingDeque<>();
	private static Queue<String> urls = new ConcurrentLinkedDeque<>();

	public static void main(String[] args) throws Exception {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
	    java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http.impl.execchain").setLevel(Level.OFF);

		double threads = 16;
		ExecutorService executor = Executors.newFixedThreadPool((int)threads);
		
		Set<String> tos = new HashSet<>(Arrays.asList("LHR","FRA","AMS","CDG"));
		for(String to : tos) {
			Set<String> froms = new HashSet<>(tos);
			String from = "LHR,FRA,AMS,CDG";
			urls.addAll(generateUrls(to, from));
		}

		for(int i = 0; i < threads*3; i++) {
			drivers.add(new AbstractMap.SimpleEntry<WebDriver, FlightsHome>(getWebDriver(), null));
    		Callable<Void> task = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					run();
					return null;
				}
			};
			executor.submit(task);
		}

		executor.awaitTermination(10, TimeUnit.MINUTES);
		executor.shutdown();
	}

	private static WebDriver getWebDriver() {
		return new HtmlUnitDriver(true);
	}

	private static void runOnDriver(String destination) throws Exception {
		WebDriver driver = getWebDriver();
		DateIterator di = new DateIterator(MIN_START_DATE, MAX_START_DATE, MAX_END_DATE, MIN_DURATION, MAX_DURATION);
		FlightsHome prev = null;
		while(di.hasNext()) {
			Date startDate = di.getStartDate();
			Date endDate = di.getEndDate();
			String url = "https://www.google.com/flights/?f=0#search;iti=SEA_" 
					+ destination +"_" + dateToString(startDate) + "*" + destination + "_SEA_"
					+ dateToString(endDate) + ";tt=m;mp=500;md=870,870"; 
			results.updateRoute(destination, startDate, endDate);
			FlightsHome home = new FlightsHomeOld(driver, url);
			try {
				if(prev == null) {
					home.load();
				} else {
					home.load(prev);
				}
				prev = home;
				List<Integer> prices = home.getPrices();
				if(prices.size() > 0) { 
					System.out.println(prices + " " + url);
				}
				di.next();
			} catch (Throwable t) {
				results.routeFailed(destination);
				prev = null;
				driver.quit();
				driver = getWebDriver();
			}
		}
		driver.quit();
		results.completeRoute(destination);
	}
	
	private static String generateUrl(String to, String from, Date startDate, Date endDate) {
		// https://www.google.com/flights/#search;iti=SEA_LHR,LGW,CGN,FRA_2018-06-15*
		// LHR,LGW,CGN,CDG_SEA_2018-07-15;tt=m;mp=650;md=960,960
		return "https://www.google.com/flights/?f=0#search;iti=SEA_" 
				+ to +"_" + dateToString(startDate) + "*" + from + "_SEA_"
				+ dateToString(endDate) + ";tt=m;mp=550;md=870,870"; 

	}
	
	private static List<String> generateUrls(String to, String from) {
		final List<String> out = Lists.newArrayList();
		final DateIterator di = new DateIterator(MIN_START_DATE, MAX_START_DATE, MAX_END_DATE, MIN_DURATION, MAX_DURATION);
		while(di.hasNext()) {
			Date startDate = di.getStartDate();
			Date endDate = di.getEndDate();
			out.add(generateUrl(to, from, startDate, endDate));
			di.next();
		}
		
		return out;
	}
	
	private static void run() throws InterruptedException {
		String url;
		while((url = urls.poll()) != null) {
			FlightsHome page = loadPage(url, 5);
			if(page != null) {
				List<Integer> prices = page.getPrices();
				if(prices.size() > 0) {
					System.out.println(prices + " " + url);
				}
			}
		}
	}
	
	private static FlightsHome loadPage(String url, int retries) throws InterruptedException {
		while(retries > 0) {
			Map.Entry<WebDriver, FlightsHome> driverToPage = drivers.poll(5, TimeUnit.MINUTES);
			WebDriver driver = driverToPage.getKey();
			FlightsHome home = new FlightsHomeOld(driver, url);
			FlightsHome prev = driverToPage.getValue();
			try {
				if(prev == null) {
					home.load();
				} else {
					home.load(prev);
				}
				driverToPage.setValue(home);
				drivers.add(driverToPage);
				return home;
			} catch (Throwable t) {
				driver.quit();
				drivers.add(new AbstractMap.SimpleEntry<WebDriver, FlightsHome>(getWebDriver(), null));
				retries--;
			}
		}
		return null;
	}
	
	private static String dateToString(Date startDate) {
		String sd = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		return sd;
	}

	private static Date toDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
}

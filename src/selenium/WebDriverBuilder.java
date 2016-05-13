/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selenium;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import site.Site;

/**
 *
 * @author sku202
 */
public class WebDriverBuilder {

	private EventFiringWebDriver driver;
	private WebDriver webDriver;

	public EventFiringWebDriver getHeadLessDriver(Site site) {
		DesiredCapabilities caps = DesiredCapabilities.phantomjs();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX, "Y");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, addCommandLineArguments());

		// User Agent Settings
		caps.setCapability("phantomjs.page.settings.userAgent", site.getUserAgent());

		// User Name & Password Settings
		if (site.hasAuthentication()) {
			caps.setCapability("phantomjs.page.settings.userName", site.getUsername());
			caps.setCapability("phantomjs.page.settings.password", site.getPassword());
		}
		webDriver = new PhantomJSDriver(caps);
		if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
			Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
			webDriver.manage().window().setSize(s);
		} else {
			webDriver.manage().window().maximize();
		}
		webDriver.manage().timeouts().implicitlyWait(site.getTimeout(), TimeUnit.MILLISECONDS);
		driver = new EventFiringWebDriver(webDriver);
		EventHandler handler = new EventHandler();
		driver.register(handler);
		return driver;
	}

	public EventFiringWebDriver getHeadLessDriver() {
		DesiredCapabilities caps = DesiredCapabilities.phantomjs();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX, "Y");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, addCommandLineArguments());
		webDriver = new PhantomJSDriver(caps);
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver = new EventFiringWebDriver(webDriver);
		EventHandler handler = new EventHandler();
		driver.register(handler);
		return driver;
	}

	public EventFiringWebDriver getHeadLessDriver(boolean auth, String username, String password) {

		DesiredCapabilities caps = DesiredCapabilities.phantomjs();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX, "Y");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, addCommandLineArguments());
		webDriver = new PhantomJSDriver(caps);
		if (auth) {
			caps.setCapability("phantomjs.page.settings.userName", username);
			caps.setCapability("phantomjs.page.settings.password", password);
		}
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver = new EventFiringWebDriver(webDriver);
		EventHandler handler = new EventHandler();
		driver.register(handler);
		return driver;
	}

	public EventFiringWebDriver getFirefoxDriver(Site site) {
		FirefoxProfile ffp = new FirefoxProfile();
		ffp.setPreference("general.useragent.override", site.getUserAgent());
		DesiredCapabilities caps = DesiredCapabilities.firefox();
		caps.setJavascriptEnabled(true);
		caps.setCapability(FirefoxDriver.PROFILE, ffp);
		caps.setCapability("takesScreenshot", true);
		// User Name & Password Settings
		webDriver = new FirefoxDriver(caps);
		if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
			Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
			webDriver.manage().window().setSize(s);
		} else {
			webDriver.manage().window().maximize();
		}
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver = new EventFiringWebDriver(webDriver);
		EventHandler handler = new EventHandler();
		driver.register(handler);
		return driver;
	}

	public EventFiringWebDriver getFirefoxDriver() {
		DesiredCapabilities caps = DesiredCapabilities.firefox();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		// User Name & Password Settings
		webDriver = new FirefoxDriver(caps);
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver = new EventFiringWebDriver(webDriver);
		EventHandler handler = new EventHandler();
		driver.register(handler);
		return driver;
	}

	private ArrayList<String> addCommandLineArguments() {
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--ignore-ssl-errors=yes"); // parameter to access https
													// page
		// cliArgsCap.add("--proxy-type=https");

		return cliArgsCap;
	}

	public void destroy() {
		try {
			if (driver != null) {
				driver.quit();
			}
			killPhantomJS();
		} catch (Exception ex) {
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void killPhantomJS() {
		String serviceName = "phtantomjs.exe";
		try {
			if (ProcessKiller.isProcessRunning(serviceName)) {
				ProcessKiller.killProcess(serviceName);
			}
		} catch (Exception ex) {
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}

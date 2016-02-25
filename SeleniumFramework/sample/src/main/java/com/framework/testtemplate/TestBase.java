package com.framework.testtemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import com.framework.exception.SeleniumException;
import com.framework.report.CaptureBrowserScreenShot;
import com.framework.report.DetailedLogs;
import com.framework.report.ExtentManager;
import com.framework.util.CommonFunctionLib;
import com.framework.util.Reader;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * TestBase.java is inherited by all test classes & contains method to setup,
 * teardown, load test data, set capabilities etc.
 */
public class TestBase {

	public WebDriver driver;
	Properties TestExecution = new Properties();
	Properties properties;
	public DetailedLogs AppLogs = new DetailedLogs();
	Reader xls = new Reader();
	CaptureBrowserScreenShot captureBrowserScreenShot = new CaptureBrowserScreenShot();
	protected ExtentReports extent = ExtentManager.getInstance();
	protected static ExtentTest test;
	
	protected TestBase() throws SeleniumException{
		FileInputStream fs;
		try {
			fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\resources\\TestExecution.properties");
			TestExecution.load(fs);
		} catch (FileNotFoundException e) {
			throw new SeleniumException("Error in connecting with Remote WebDriver"+e);
		} catch (IOException e) {
			throw new SeleniumException("Error in connecting with Remote WebDriver"+e);
		}
	}
	
	@BeforeMethod
	@Parameters("browserName")
	public void beforeMethod(Method caller,String browserName) throws SeleniumException, MalformedURLException {
		startWebDriver(browserName);
		test = extent.startTest(caller.getName(), "Sample description");
	}

	/**
	 * Purpose : This method starts browser on available node & connect wih HUB
	 * 
	 * @param hubAddress
	 * @throws MalformedURLException
	 * @throws SeleniumException
	 */
	public void startWebDriver(String browserName) throws SeleniumException, MalformedURLException {
	AppLogs.info("TestBase->startDriver starts..");
		initPropertiesFile();
		if (TestExecution.getProperty("Run").toUpperCase().trim().equals("HUB")){
			if (browserName.toUpperCase().trim().equals("FIREFOX")){
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), generateDesiredCapabilities(browserName.toUpperCase()));
			}else if (browserName.toUpperCase().trim().equals("CHROME")){
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), generateDesiredCapabilities(browserName.toUpperCase()));
			}
		
		}else if(TestExecution.getProperty("Run").toUpperCase().trim().equals("LOCAL")){	
			if (browserName.toUpperCase().trim().equals("FIREFOX")){
				driver = new FirefoxDriver(generateDesiredCapabilities("FIREFOX"));
			}else if(browserName.toUpperCase().trim().equalsIgnoreCase("CHROME")){
				System.setProperty("webdriver.chrome.driver","C:\\Users\\Shankar\\Downloads\\chromedriver_win32\\chromedriver.exe");
				driver = new ChromeDriver(generateDesiredCapabilities("CHROME"));
			}else if(browserName.toUpperCase().trim().equalsIgnoreCase("IE")){
				System.setProperty("webdriver.ie.driver","C:\\Users\\Shankar\\Downloads\\IEDriverServer_x64_2.47.0\\IEDriverServer.exe");
				driver = new InternetExplorerDriver(generateDesiredCapabilities("IE"));
			}
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get(TestExecution.getProperty("BaseURL"));
		AppLogs.info("TestBase->startDriver ends..");
	}

	/**
	 * Purpose : This method close/quit driver once test class execution is
	 * complete
	 */
	@AfterClass(alwaysRun = true)
	public void stopDriver() {
		AppLogs.info("TestBase -> stopDriver starts..");
		if (driver != null) {
			driver.quit();
			driver = null;
		}
		AppLogs.info("TestBase -> stopDriver ends..");
	}

	/**
	 * Purpose : This method reads data from TestDate file for respective test
	 * case
	 * 
	 * @param testName
	 * @return
	 */
	@DataProvider
	public Object[][] readData(Method testName) {
		AppLogs.info("readData starts..");
		return CommonFunctionLib.readData(testName.getName(), xls);
	}

	@AfterSuite
	public void afterSuite(){
		extent.flush();
        extent.close();
	}
	
	/**
	 * Purpose : This method performs tearDown after a test case execution &
	 * takes browser screen shot in case of test fail
	 * @param result
	 * @throws SeleniumException
	 * @throws IOException
	 */
	@AfterMethod(alwaysRun = true)
	public void TearDown(ITestResult result, Method testName) throws SeleniumException, IOException {
		AppLogs.info("-------------" + testName.getName()+ " ---------- TearDown starts..");
		if (!result.isSuccess()) {
			test.log(LogStatus.FAIL, "Snapshot below: " + test.addScreenCapture(captureBrowserScreenShot.takeScreenShots(driver)));
		}
		extent.endTest(test);
		stopDriver();
		AppLogs.info("-------------" + testName.getName()	+ " ---------- TearDown ends..");
	}

	/**
	 * Purpose : This method set capability in browser
	 * 
	 * @param capabilityType
	 * @return
	 */
	private DesiredCapabilities generateDesiredCapabilities(String capabilityType) {
		AppLogs.info("TestBase -> generateDesiredCapabilities starts.. "+capabilityType);
		DesiredCapabilities capabilities=null;

		switch (capabilityType) {
		case "IE":
			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION,true);
			capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
			capabilities.setCapability("requireWindowFocus", true);
			capabilities.setBrowserName("iexplore");
			capabilities.setPlatform(org.openqa.selenium.Platform.WINDOWS);
			capabilities.setCapability("takesScreenShot", true);
			System.setProperty("webdriver.ie.driver",System.getProperty("user.dir")+"\\Binary\\IEDriverServer.exe");
			break;
		case "CHROME":
			capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches",Arrays.asList("--no-default-browser-check"));
			HashMap<String, String> chromePreferences = new HashMap<String, String>();
			chromePreferences.put("profile.password_manager_enabled", "false");
			capabilities.setCapability("chrome.prefs", chromePreferences);
			capabilities.setCapability("takesScreenShot", true);
			System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"\\Binary\\chromedriver.exe"); 
			break;
		case "FIREFOX":
			capabilities = DesiredCapabilities.firefox();
			capabilities.setBrowserName("firefox");
			capabilities.setCapability("takesScreenShot", true);
			capabilities.setCapability("acceptSSLCerts", true);
			capabilities.setPlatform(org.openqa.selenium.Platform.ANY);
			break;
		default:
			capabilities = DesiredCapabilities.htmlUnit();
			capabilities.setCapability("javascriptEnabled", "true");
		}
		AppLogs.info("TestBase -> generateDesiredCapabilities ends.. "+capabilityType);
		return capabilities;
	}

	/**
	 * Purpose : Loads .propertis file
	 * @throws SeleniumException
	 */
	private void initPropertiesFile() throws SeleniumException {
		properties = new Properties();
		try {
			FileReader Seleniumreader = new FileReader(System.getProperty("user.dir")+ "\\src\\test\\resources\\TestExecution.properties");
			properties.load(Seleniumreader);
		} catch (IOException e) {
			throw new SeleniumException("Failed to load Properties file" + e);
		}
	}
}

/**
 * java -Dwebdriver.chrome.driver="D:\Code\SeleniumFramework\Binary\chromedriver.exe" -jar selenium-server-standalone-2.47.1.jar -role node -hub http://localhost:4444/grid/register -port 5555 -browser browserName=chrome -maxSession 1
 * java -jar selenium-server-standalone-2.47.1.jar -role node -hub http://localhost:4444/grid/register -port 5556 -browser browserName=firefox -maxSession 1
 */
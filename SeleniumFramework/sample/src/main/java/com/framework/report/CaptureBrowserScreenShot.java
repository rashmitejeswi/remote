package com.framework.report;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.framework.exception.SeleniumException;


public class CaptureBrowserScreenShot{
	
	DetailedLogs AppLogs = new DetailedLogs();
	public CaptureBrowserScreenShot(){
		
	}
	
/**
 * Purpose : This method generates unique file name
 * @return GetDateTime
 * @throws SeleniumException
 */
private String getDateTime() throws SeleniumException {
	AppLogs.info("CaptureBrowserScreenShot -> getDateTime() - starts..");
	try {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		String strTime = sdfTime.format(now);
		strTime = strTime.replace(":", "-");
		AppLogs.info("TestBase -> getDateTime() - ends..");
		return (strDate + "_" + strTime);
	}
	catch (Exception e) {
		throw new SeleniumException("CaptureBrowserScreenShot -> getDateTime() - ", e);
	}
}

/**
 * Purpose : This method takes screenshot
 * @throws IOException 
 * @throws Exception 
 */
public String takeScreenShots(WebDriver driver) throws SeleniumException, IOException {
	AppLogs.info("CaptureBrowserScreenShot -> takeScreenShots() - starts..");
    try {
        File temp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File stored = new File(System.getProperty("user.dir")+ "\\Report\\Screenshot\\" + getDateTime() + ".png");
        FileUtils.copyFile(temp, stored);
        AppLogs.info("CaptureBrowserScreenShot -> takeScreenShots() - ends..");
        return stored.toString();
    }catch (WebDriverException somePlatformsDontSupportScreenshots) {
    	throw new SeleniumException("CaptureBrowserScreenShot -> takeScreenShots() - ", somePlatformsDontSupportScreenshots);
    }catch(IOException e){
    	throw new SeleniumException("CaptureBrowserScreenShot -> takeScreenShots() - ", e);
    }
}

/**
 * Purpose : This method embeds screenshot into Cucumber Jenkins report
 * @param scenario
 * @throws SeleniumException
 * @throws IOException 
 */
 public void embedScreenShotIntoReport(WebDriver driver) throws SeleniumException, IOException{
	AppLogs.info("CaptureBrowserScreenShot -> embedScreenShotIntoReport() - starts..");
	     try {  
	    	 takeScreenShots(driver);
         } catch (WebDriverException wde) {  
        	 throw new SeleniumException("CaptureBrowserScreenShot -> embedScreenShotIntoReport() - ", wde);
         } catch (ClassCastException cce) {  
        	 throw new SeleniumException("CaptureBrowserScreenShot -> embedScreenShotIntoReport() - ", cce);
         } catch(IOException Io){
        	 throw new SeleniumException("CaptureBrowserScreenShot -> embedScreenShotIntoReport() - ", Io);
         } catch(Exception e){
        	 throw new SeleniumException("CaptureBrowserScreenShot -> embedScreenShotIntoReport() - ", e);
         }
     AppLogs.info("CaptureBrowserScreenShot -> embedScreenShotIntoReport() - ends..");
  }  
}
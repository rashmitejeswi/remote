package com.framework.actions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.framework.exception.SeleniumException;
import com.framework.report.DetailedLogs;


/**
 * WebAction.java is for interacting with browser objects. Page objects re-use these utilities to perform functions relevant to test steps. 
 * @author 
 * @version 1.0 
 */
public class WebAction {
	
	protected WebDriver driver = null;
	protected WebDriver webDriver = null;
	public DetailedLogs AppLogs = new DetailedLogs();
	final int waitvalue = 10;
	final int timeout = 10;
	private static final long DEFAULT_POLL_MILLIS = 100;
	Actions builder = null;

	public static final int CLICK_TIMEOUT_SECONDS = 5;
    public static final int PRESENCE_TIMEOUT_SECONDS = 5;
    public static final int POLLING_WITH_REFRESH_TIMEOUT_SECONDS = 30;
    public static final int REFRESH_TIMEOUT_SECONDS = 5;
    public static final int SHORT_TIMEOUT_SECONDS = 1;
    public static final int MEDIUM_TIMEOUT_SECONDS = 5;
    public static final int LONG_TIMEOUT_SECONDS = 20;
    public static final int PAUSE_BETWEEN_KEYS_MILLIS = 50;
    public static final int PAUSE_BETWEEN_TRIES_MILLIS = 200;
    public static final int PAUSE_BETWEEN_REFRESH_SECONDS = 5;
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = 80;
    public static final int PAGE_READY_TIMEOUT_SECONDS = 10;
    public static final int IMPLICIT_WAIT_TIMEOUT_MILLIS = 2000;
	
	/**
	 * Constructor to initialize WebAction class objects
	 * @param driver
	 */
	public WebAction(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(this.driver, this);
		builder = new Actions(this.driver);
		AppLogs = new DetailedLogs();
	}


    /**
     * @param el
     * @return
     * @throws SeleniumException
     */
    public WebElement clearText(WebElement el) throws SeleniumException {
        String tag = el.getTagName();
        try {
        	waitUntilClickable(el,CLICK_TIMEOUT_SECONDS);
            el.clear();
            AppLogs.info("Cleared text from element <{}>", tag);
        } catch (Exception e) {
            throw new SeleniumException("WebAction -> clearText(WebElement el) - Error clearing text from element <%s>: %s"+ tag+ e.getMessage());
        }
        return el;
    }
    
    /**
     * 
     * @param el
     * @param value
     * @throws SeleniumException
     */
    public void inputText(WebElement el, String value) throws SeleniumException{
	   String tag = el.getTagName();
        try {
        	isClickable(el);
            clearText(el);
            el.sendKeys(value);
            AppLogs.info("Enter text into element <{}>"+ tag);
        } catch (Exception e) {
            throw new SeleniumException("WebAction -> inputText(WebElement el, String value) - Error entering text into element <%s>: %s"+ tag+ e.getMessage());
        }
    }
 
	/**
	 * Purpose : Parameterized version, This method Type special key like Shift,Backspace, Enter, Tab, Shift along with text
	 * @param object
	 * @param input
	 */
	public void inputText(WebElement el,Keys theKey, String input) throws SeleniumException{
		AppLogs.info("EnterValueTextWithShit starts.." + "1st Arg : "+el.getTagName() + "2nd Arg : "+ input);
		 try{
			 el.sendKeys(Keys.chord(Keys.SHIFT,input));
		 }catch(IllegalArgumentException e){
			 throw new SeleniumException("WebAction -> EnterValueText(WebElement object,Keys theKey, String input)" + e);
		 }
		 AppLogs.info("EnterValueTextWithShit ends..");
	}
	
    /**
     * 
     * @param el
     * @return
     * @throws SeleniumException
     */
   public WebElement click(WebElement el) throws SeleniumException {
	   String tag = el.getTagName();
	   try{
	    waitUntilClickable(el,CLICK_TIMEOUT_SECONDS);
        el.click();
        AppLogs.info("Click performed on element <{}>"+ tag);
	   }catch(NoSuchElementException e){
		   throw new SeleniumException("WebAction -> click() - Error in click operation on element <%s>: %s"+ tag+ e.getMessage());
	   }
       return el;
    }
   
   public boolean isDisplayed(WebElement el) {
   	AppLogs.info("---- WebAction -> isDisplayed starts");
       if (el == null) {
           return false;
       }
       try {
           if (!el.isDisplayed()) { 
               return false;
           }
           if (el.getSize().getHeight() <= 0 || el.getSize().getWidth() <= 0) { // If width or height is 0, element is not clickable
               return false;
           }
       } catch (Exception e) {
           return false;
       }
       AppLogs.info("---- WebAction -> isDisplayed ends");
       return true;
   }
   
   /***
    * check if weblement is clickable
    * @param el
    * @return
    */
    public boolean isClickable(WebElement el) {
    	AppLogs.info("---- WebAction -> isClickable starts");
        if (el == null) {
            return false;
        }
        try {
            if (!el.isDisplayed()) { 
                return false;
            }
            if (!el.isEnabled()){ 
            	 return false;
            }
            if (el.getSize().getHeight() <= 0 || el.getSize().getWidth() <= 0) { // If width or height is 0, element is not clickable
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        AppLogs.info("---- WebAction -> isClickable ends");
        return true;
    }

 
    public WebElement waitUntilClickable(final WebElement el, int timeout) {
    	AppLogs.info("---- WebAction -> waitUntilClickable starts");
        int waitSeconds = timeout;
        final String message = "Element never became clickable after '%d' seconds"+waitSeconds;
        WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
        wait.withMessage(message).ignoring(StaleElementReferenceException.class);
        wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver webDriver) {
                if (isClickable(el)) {
                    return el;
                }
                return null;
            }
        });
        AppLogs.info("---- WebAction -> waitUntilClickable ends");
        return el;
    }
  
    /**
     * @param el
     * @return
     * @throws SeleniumException
     */
    public String getPageTitle() throws SeleniumException {
    	String title = null;
        try {
        	title = driver.getTitle();
        	AppLogs.info("Title on page <{}>", title);
        } catch (Exception e) {
            throw new SeleniumException("WebAction -> clearText(WebElement el) - Error clearing text from element <%s>: %s"+ e.getMessage());
        }
        return title;
    }
    
    /**
     * @param el
     * @return
     * @throws SeleniumException
     */
    public String getUrl() throws SeleniumException {
    	String url = null;
        try {
        	url = driver.getCurrentUrl();
        	AppLogs.info("Title on page <{}>", url);
        } catch (Exception e) {
            throw new SeleniumException("WebAction -> clearText(WebElement el) - Error clearing text from element <%s>: %s"+ e.getMessage());
        }
        return url;
    }
	
	/**
	 * Purpose : This is used to fetch the CSS properties' values of the given element. CSS properties
	 * can be font-family, background-color, color, and so on.
	 * @param object
	 * @param ProprertyName
	 */
	public void getCSS(WebElement object, String ProprertyName) {
		AppLogs.info("---- WebAction -> getCSS starts.."+ "1st Arg : "+object.getTagName() + "2nd Arg : "+ ProprertyName);
		object.getCssValue(ProprertyName);
		// getCssValue("font-family"));
		// getCssValue("background-color"));
		AppLogs.info("---- WebAction -> getCSS ends..");
	}

	/***
	 * Purpose : This is used to get the relative position of an element where it is rendered on
	 * the web page.
	 * @param object
	 */
	public void getLocation(WebElement object) {
		AppLogs.info("---- WebAction ->getLocation() starts.."+ "1st Arg : "+object.getTagName());
		object.getLocation();
		AppLogs.info("---- WebAction ->getLocation() ends..");
	}

	/***
	 * Purpose : It will return the width and height of the rendered WebElement.
	 * @param object
	 */
	public void getSize(WebElement object) {
		AppLogs.info("---- WebAction ->getSize() starts.."+ "1st Arg : "+object.getTagName());
		object.getSize();
		AppLogs.info("---- WebAction ->getSize() ends..");
	}

	/***
	 * Purpose : It will give the visible text if the element contains any text on it or else will return
	 * nothing.
	 * @param object
	 * @throws SeleniumException 
	 */
	public String getText(WebElement el) throws SeleniumException {
		AppLogs.info("---- WebAction ->getText starts.."+ "1st Arg : "+el.getTagName());
		String tag = el.getTagName();
		try{
			waitUntilClickable(el,CLICK_TIMEOUT_SECONDS);
			AppLogs.info("Text read from element <{}>", tag);
			return el.getText();
		} catch(Exception e) {
	        throw new SeleniumException("WebAction -> clearText(WebElement el) - Error clearing text from element <%s>: %s"+ tag+ e.getMessage());
	    }
	}
	
	/***
	 * Purpose : This will return the tag name of the WebElement. For example, in the following HTML
	 * code, button is the tag name of the HTML element
	 * @param object
	 */
	public void getTagName(WebElement object) {
		AppLogs.info("---- WebAction ->getTagName starts.."+ "1st Arg : "+object.getTagName() );
		object.getTagName();
		AppLogs.info("---- WebAction ->getTagName ends..");
	}
	/***
	 * Purpose : Verifies if an element is selected right now on the web page and can be executed only on a radio button, options in select,
	 * and checkbox WebElements.
	 * @param object
	 */
	public void isSelected(WebElement object) {
		AppLogs.info("---- WebAction ->isSelected starts.."+ "1st Arg : "+object.getTagName());
		object.isSelected();
		AppLogs.info("---- WebAction ->isSelected ends..");
	}

	/***
	 * Purpose : Verifies if an element is selected right now on the web page and can be executed only on a radio button, options in select,
	 * and checkbox WebElements.
	 * @param object
	 */
	public void select(WebElement object, String value) {
		AppLogs.info("---- WebAction ->isSelected starts.."+ "1st Arg : "+object.getTagName());
		Select select = new Select(object);
		select.selectByVisibleText(value);
		AppLogs.info("---- WebAction ->isSelected ends..");
	}
	
	public void select(WebElement object) {
		AppLogs.info("---- WebAction ->isSelected starts.."+ "1st Arg : "+object.getTagName());
		Select select = new Select(object);
		select.selectByIndex(1);
		AppLogs.info("---- WebAction ->isSelected ends..");
	}
	// ################## Mouse Operation #################################
	/**
	 * Purpose : The moveByOffset() method is used to move the mouse from its current position to another point on the web page. User can specify the X
	 * distance and Y distance the mouse has to be moved. When the page is loaded, generally the initial position of a mouse would be (0, 0),
	 * @param object
	 * @param ToXPoint
	 * @param ToYPoint
	 */
	public void mouseOffSet(WebElement object, int ToXPoint, int ToYPoint) {
		AppLogs.info("---- WebAction ->mouseOffset starts.."+ "1st Arg : "+object.getTagName() + "2nd Arg : "+ToXPoint+ "3rd Arg : "+ToYPoint);
		builder.moveByOffset(object.getLocation().getX() + ToXPoint, object.getLocation().getY() + ToYPoint);
		builder.perform();
		AppLogs.info("---- WebAction ->mouseOffset ends..");
	}

	/**
	 * Purpose : The click() method is used to simulate the left-click of your mouse at its current point of location. This method doesn't really realize where
	 * or on which element it is clicking. It just blindly clicks wherever it is at that point of time. Hence, this method is used in combination with
	 * some other action rather than independently, to create a composite action
	 * @param object
	 * @param toXposition
	 * @param toYposition
	 */
	public void click(WebElement object, int toXposition, int toYposition) {
		AppLogs.info("---- WebAction ->click starts.."+ "1st Arg : "+object.getTagName() + "2nd Arg : "+toXposition+ "3rd Arg : "+toYposition);
		builder.moveByOffset(object.getLocation().getX() + toXposition,object.getLocation().getY() + toXposition).click();
		builder.perform();
		AppLogs.info("---- WebAction ->click ends..");
	}
	
	/**
	 * Purpose : click() method to click directly on the WebElement.
	 * @param object
	 */
	public void ClickOnWebElement(WebElement object) {
		AppLogs.info("ClickOnWebElement starts.."+ "1st Arg : "+object.getTagName());
		builder.click(object);
		builder.build().perform();
		AppLogs.info("ClickOnWebElement ends..");
	}

	/***
	 * Purpose : The clickAndHold()method is another method of the Actions class that left-clicks on an element and holds it without releasing the left button
	 * of the mouse. This method will be useful when executing operations such as drag-and-drop.
	 * @param object
	 */
	public void clickAndHold(WebElement object) {
		AppLogs.info("---- WebAction -> clickAndHold starts.."+ "1st Arg : "+object.getTagName());
		builder.moveByOffset(200, 20).clickAndHold().moveByOffset(120, 0).perform();
		AppLogs.info("---- WebAction -> clickAndHold ends..");
	}

	
	/**
	 * Purpose : taken on a held WebElement is to release it so that the element can be dropped or released from the mouse.
	 * @param source
	 * @param target
	 */
	public void clickAndHoldAndRelease(WebElement source, WebElement target) {
		AppLogs.info("---- WebAction ->ClickAndHoldAndRelease starts.."+ "1st Arg : "+source.getTagName()+ " 2nd Arg : "+target.getTagName());
		builder.clickAndHold(source).release(target).perform();
		AppLogs.info("---- WebAction ->ClickAndHoldAndRelease ends..");
	}

	/**
	 * Purpose : helps us to move the mouse cursor to a WebElement on the web page.
	 * @param object
	 */
	public void moveToElement(WebElement object) {
		AppLogs.info("---- WebAction ->moveToElement starts.." +"1st Arg : "+object.getTagName());
		builder.moveToElement(object).clickAndHold().moveByOffset(120, 0).perform();
		AppLogs.info("---- WebAction ->moveToElement ends..");
	}

	/**
	 * Purpose :  have to drag-and-drop components or WebElements of a web page.
	 * @param object
	 */
	public void dragMe(WebElement object) {
		AppLogs.info("---- WebAction ->dragMe starts.."+"1st Arg : "+object.getTagName());
		builder.dragAndDropBy(object, 300, 200).perform();
		AppLogs.info("---- WebAction ->dragMe ends..");
	}

	/**
	 * Purpose : The only difference being that instead of moving the WebElement by an offset, we move it on to a target element
	 * @param source
	 * @param target
	 */
	public void dragAndDropTo(WebElement source, WebElement target) {
		AppLogs.info("---- WebAction ->dragAndDropTo starts..");
		builder.dragAndDrop(source, target).perform();
		AppLogs.info("---- WebAction ->dragandDropTo ends..");
	}

	/**
	 * Purpose : Moving on to another action that can be performed using mouse, doubleClick()is another out of the box method that WebDriver provides to
	 * emulate the double-clicking of the mouse.
	 * @param object
	 */
	public void doubleClick(WebElement object) {
		AppLogs.info("---- WebAction ->doubleClick starts.."+"1st Arg : "+object.getTagName());
		builder.moveToElement(object).doubleClick().perform();
		AppLogs.info("---- WebAction ->doubleClick ends..");
	}

	/**
	 * Purpose : The contextClick() method, also known as right-click, is quite common on many web pages these days. The context is nothing but a menu; a list of
	 * items is associated to a WebElement based on the current state of the web page.
	 * @param object
	 * @param item4
	 */
	public void rightClick(WebElement object, String item4) {
		AppLogs.info("---- WebAction ->rightClick starts.."+"1st Arg : "+object.getTagName());
		builder.contextClick(object).click(driver.findElement(By.name("Item 4"))).perform();
		AppLogs.info("---- WebAction ->rightClick ends..");
	}
	
	/**
	 * Purpose : returns HTTP code for a given URL
	 * @param urlString
	 * @return
	 * @throws IOException 
	 */
	public boolean getResponseCode(String urlString) throws SeleniumException, IOException{
		AppLogs.info("---- WebAction ->getResponseCode starts.. for URL : "+urlString);
	    boolean isValid = false;
	    HttpURLConnection httpURLConnect = null;
	    int code = 0;
        try {
            URL url = new URL(urlString);
            httpURLConnect = (HttpURLConnection)url.openConnection();
            httpURLConnect.setRequestMethod("GET");
            httpURLConnect.connect();
            code = httpURLConnect.getResponseCode();
            if (code == 200){
            	AppLogs.debug(urlString +" - "+httpURLConnect.getResponseMessage());
            	isValid = true;
            }
            if(code == HttpURLConnection.HTTP_NOT_FOUND)  
            {
            	AppLogs.debug(urlString +" - "+httpURLConnect.getResponseMessage() + " - "+ HttpURLConnection.HTTP_NOT_FOUND);
            	isValid = true;
            }
         
         } catch (MalformedURLException e) {
            throw new SeleniumException("MalformedURLException Error : "+e +" , "+ urlString);
         } catch (IOException e) {
        	   	AppLogs.debug(urlString +" - "+httpURLConnect.getResponseMessage() + " - "+ HttpURLConnection.HTTP_NOT_FOUND);
            	isValid = true;
        	  throw new SeleniumException("IOException Error : "+e+" , "+ urlString);
         } catch (Exception e) {
        	  throw new SeleniumException("Exception Error : "+e+" , "+ urlString);
         }
        AppLogs.info("---- WebAction ->getResponseCode ends..");
        return isValid;
    }
	
	/**
	 * Purpose : This method is useful when you have multiple links on a page and instead of click each link and verifying the page 
	 * title or what's written on the page - you can just send an http request to the link and see what the response is  
	 * @param link
	 * @return
	 * @throws SeleniumException
	 */
	public boolean isFileDownloadable(String link) throws SeleniumException{
		AppLogs.info("---- WebAction ->isFileDownloadable starts.. for URL : "+link);
		boolean isValid = false;
		AppLogs.debug("Link: " + link);
		try {
			if (getResponseCode(link)){
				isValid = true;
			}
		} catch (Exception e) {
			throw new SeleniumException("isFileDownloadable ends.."+e);
		}
		AppLogs.info("---- WebAction ->isFileDownloadable ends..");
		return isValid;
	}
		
	/**
	 * Purpose : Verifies if link on a given page is broken
	 * @throws SeleniumException
	 * @throws IOException 
	 */
	public String isLinkBroken() throws SeleniumException, IOException{
	   AppLogs.info("---- WebAction ->isLinkBroken starts.. for URL : "+driver.getCurrentUrl());
	   String link = null;
       List <WebElement>linksList = driver.findElements(By.tagName("a")); 
        for(WebElement linkElement: linksList){
          link =linkElement.getAttribute("href");
          if(link!=null){
            if (getResponseCode(link)){
            	Reporter.log("isLinkBroken : "+link + "  works fine");
            }else {
            	Reporter.log("isLinkBroken : "+link + "  is invalid");
            }
          }
      }
        AppLogs.info("---- WebAction ->isLinkBroken starts.. for URL : "+driver.getCurrentUrl());
      return link;
  } 
  
	/**
	 * Purpose : Parameterize form of isLinkBroken method, verifies if link on a given page is broken
	 * @throws SeleniumException
	 * @throws IOException 
	 */
	public boolean isLinkBroken(String urlString) throws SeleniumException, IOException{
	   AppLogs.info("---- WebAction ->isLinkBroken starts .. for URL : "+ urlString);
	    boolean isValid = false;
	      if(urlString!=null){
            if (getResponseCode(urlString)){
            	isValid = true;
            }else {
            	isValid = false;
            }
          }
	    AppLogs.info("---- WebAction ->isLinkBroken ends .. for URL : "+ urlString);
      return isValid;
  } 
	
	//---------------------Working on Multiple Browser/Switching Frame/Handling Alert-------------------------
	/**
	 * Purpose : This method switches to browser based on browser URL
	 * @param currentUrl
	 */
	public void switchToBrowser(String title) {
		AppLogs.info("WebAction -> switchToBrowser starts.."+driver.getCurrentUrl());
		for(String winHandle :driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
			if(driver.getTitle().contains(title)){
				AppLogs.info("You are in required window : " + driver.getCurrentUrl());
				break;
            } 
			else{
				AppLogs.error("URL of the page after - switchingTo: " + driver.getCurrentUrl());
			}
		}
		AppLogs.info("WebAction -> switchToBrowser ends");
	}
	
	/**
	 * Purpose : switch target to a Frame of a browser
	 * @param frameIndex
	 */
	public void switchToFrame(String frameIndex){
		AppLogs.info("WebAction -> switchToFrame() starts..");
		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		AppLogs.info("WebAction -> switchToFrame() ends..");
	}      
	
	/**
	 * Purpose : switch target to a second Frame of a browser where target is set to first frame
	 * @param frameIndex
	 */
	public void switchToFrame(String frameIndex1,String frameIndex2){
		AppLogs.info("WebAction -> switchToFrame() starts..");
		driver.switchTo().frame(frameIndex1);
		driver.switchTo().defaultContent();
		driver.switchTo().frame(frameIndex2);
		AppLogs.info("WebAction -> switchToFrame() ends..");
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean acceptAlert() throws SeleniumException {
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			return true;
		}catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> AcceptAlert() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> AcceptAlert() , "+e);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean declineAlert() throws SeleniumException {
		try {
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
			return true;
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> DeclineAlert() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> DeclineAlert() , "+e);
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws SeleniumException
	 */
	public boolean inputTextOnAlert(String keysToSend) throws SeleniumException {
		try {
			Alert alert = driver.switchTo().alert();
			alert.sendKeys(keysToSend);
			return true;
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> inputTextOnAlert() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> inputTextOnAlert() , "+e);
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws SeleniumException
	 */
	public String getTextOfAlert() throws SeleniumException {
		try {
			Alert alert = driver.switchTo().alert();
			return alert.getText();
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> getTextOfAlert() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> getTextOfAlert() , "+e);
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws SeleniumException
	 */
	public void refresh() throws SeleniumException {
		try {
			driver.navigate().refresh();
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> refresh() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> refresh() , "+e);
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws SeleniumException
	 */
	public void forward() throws SeleniumException {
		try {
			driver.navigate().forward();
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> forward() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> forward() , "+e);
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws SeleniumException
	 */
	public void back() throws SeleniumException {
		try {
			driver.navigate().back();
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> back() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> back() , "+e);
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws SeleniumException
	 */
	public boolean openURL(String URL) throws SeleniumException {
		boolean isValid = false;
		try {
			driver.navigate().to(URL);
			if (getResponseCode(URL)){
				AppLogs.info(URL + " successfully opened");
				isValid = true;
			}else {
				AppLogs.info(URL + " not working");
				isValid = false;
			}
		} catch(NoAlertPresentException e){
			throw new SeleniumException("WebAction -> openURL() , "+e);
		}
		catch (Exception e) {
			throw new SeleniumException("WebAction -> openURL() , "+e);
		}
		return isValid;
	}
   
    public Object executeJavascript(String script) {
        AppLogs.info("Executing javascript: '{}'", script);
        try {
            return ((JavascriptExecutor) webDriver).executeScript(script);
        } catch (Exception e) {
            throw new RuntimeException("Exception executing Javascript '%s':"+ script+ e);
        }
    }

    public boolean isTextPresent(String txtValue){
        boolean b = false;
        try{
        	b = driver.getPageSource().contains(txtValue);
        return b;
        }
        catch (NoSuchElementException e){
        	new SeleniumException("WebAction -> isTextPresent(String txtValue) - error"+e);
        }     
     return b;
    }
}

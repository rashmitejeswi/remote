package com.core;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.report.IReport;
import com.util.IUtility;

/**
 * CExecutionDetail.java 
 * Purpose: This class contains implementation of
 * interface IUtility, IReport, IConfig, IDBConnection, IExcelReader, IKeyword,
 * ILog
 * 
 * @author : Abhay Bharti
 * @version 1.0 21/05/14
 */
public class CExecutionDetail implements IExcelReader, IKeyword, IReport,
		IDBConnection, IConfig, IUtility, ILog {

	// Variable declaration for IExcelReader interface

	static String suite[][], TestCase[][];

	static String sFeature, sRun, sDescription, sWindow, sObject, sAction,
			sData, tData, sTitle, sBrowser, sObject_Identifier, sProperty;

	static String[] sTestCase_ID, sExecute, sTestCase_Name, sIteration,
			sFunctional_Area, tComment, tAction, tLabelName, tObjectName,
			tParent_Object, tInputData, tExpectedData;

	protected static File inputWorkbook;

	static int iFeatureCount = 0;

	static int iStepCount;

	// variable declaration for iKeyword interface

	static Keyword_Name kAction;

	static int iTime = 5000;

	static WebDriver driver;

	static WebElement element;

	static By bProperty;

	static boolean fBrowser, TestCaseStatus = true;

	Sheet sheet;

	// Variable declaration for IReport interface

	static BufferedWriter output = null;
	static File file;
	static String sDateTime, sFilename, sBodyText;
	String screenshotpath, strDesc;
	String TestExecutionTime;
	static String htmlreport;

	// Variable declaration for IUtility interface
	static int Timeout = 30;

	// ************************************************************************************************************
	// *** Constructor Implementation***
	// ************************************************************************************************************
	/**
	 * Purpose : Constructor method for class CExecutionDetail
	 */
	public CExecutionDetail() {

	}

	// ************************************************************************************************************
	// *** Implementation of IDBConnection Interface ***
	// ************************************************************************************************************
	/**
	 * Purpose : This function create connection with Database
	 * 
	 * @throws
	 */
	public void CreateConnection() throws ClassNotFoundException, SQLException {
		// TO be Implemented
	}

	/**
	 * Purpose : This function returns value from DB Table
	 * 
	 * @param query
	 * @param ReturnColumnName
	 * @return
	 */
	public String GetDBValue(String query, String ReturnColumnName) {
		// To be Implemented
		return ReturnColumnName;
	}

	// ************************************************************************************************************
	// *** Implementation of IExelReader interface ***
	// ************************************************************************************************************
	/**
	 * Purpose :This function loads Master Sheet data in Array
	 */
	public void GetSuite() {
		info("GetSuite() method starts..");

		sTestCase_ID = new String[500];

		sExecute = new String[500];

		sTestCase_Name = new String[500];

		sIteration = new String[500];

		sFunctional_Area = new String[500];

		inputWorkbook = new File(System.getProperty("user.dir")
				+ "\\src\\com\\testcase\\Controller.xls");

		debug("Test Suite Path :- " + inputWorkbook);

		Workbook w;

		try {

			try {
				w = Workbook.getWorkbook(inputWorkbook);
				sheet = w.getSheet("Master");
			} catch (IOException e) {

				error("Reading Master Sheet :-" + e);
			}
			suite = new String[sheet.getRows()][sheet.getColumns()];

			debug("Number of Row in Master Sheet :- " + sheet.getRows());

			debug("Number of Column in Master Sheet :- " + sheet.getColumns());

			for (int j = 0; j < sheet.getColumns(); j++) {

				for (int i = 0; i < sheet.getRows(); i++) {

					Cell cell = sheet.getCell(j, i);

					if (cell.getContents() != null) {

						suite[i][j] = cell.getContents();
					}

				}

			}

			// Load Master sheet which has list of test case to be executed
			for (int k = 1; k < sheet.getRows(); k++) {

				if (((suite[k][1] != null) && ((char) suite[k][1].charAt(0)) == 'Y')) {

					sTestCase_ID[iFeatureCount] = suite[k][0];

					sExecute[iFeatureCount] = suite[k][1];

					sTestCase_Name[iFeatureCount] = suite[k][2];

					sIteration[iFeatureCount] = suite[k][3];

					sFunctional_Area[iFeatureCount] = suite[k][4];

					debug("Suite : - Test Case ID : "
							+ sTestCase_ID[iFeatureCount] + " , Execute : "
							+ sExecute[iFeatureCount] + " , Test Case Name : "
							+ sTestCase_Name[iFeatureCount] + " , Iteration : "
							+ sIteration[iFeatureCount]
							+ " , Functional Area : "
							+ sFunctional_Area[iFeatureCount]
							+ "Test Case Count :" + iFeatureCount);

					iFeatureCount = iFeatureCount + 1;

				}

			}

		} catch (BiffException e) {

			error("In GetSuiteMethod, Outer Try block : - " + e);

		}

		info("GetSuite() method ends..");
	}

	// ************************************************************************************************************
	// *** GET STEPS FOR TEST SCENARIO EXECUTION ***
	// ************************************************************************************************************
	/**
	 * Purpose : This function loads Test Steps for Test Scenario Execution
	 */
	public void GetTestCase() {
		info("GetTestCase method starts..");

		tComment = new String[1000];

		tAction = new String[1000];

		tLabelName = new String[1000];

		tObjectName = new String[1000];

		tParent_Object = new String[1000];

		tInputData = new String[1000];

		tExpectedData = new String[1000];

		try {

			CreateReport();

			for (int iLoop1 = 0; iLoop1 < iFeatureCount; iLoop1++) {

				debug("Inside iLoop1 value of iFeatureCount" + iFeatureCount);

				debug("Inside iLoop1 value of iLoop1" + iLoop1);

				Workbook w;

				w = Workbook.getWorkbook(inputWorkbook);

				Sheet sheet = w.getSheet(sTestCase_ID[iLoop1]);

				debug("Test Case Worksheet :- " + sTestCase_ID[iLoop1]);

				int row = sheet.getRows();

				int iColCount = sheet.getColumns();

				debug("Number of Row in Test Script :- " + row);

				debug("Number of Column in Test Script :- " + iColCount);

				TestCase = new String[row][iColCount];

				for (int j = 0; j < iColCount; j++) {

					for (int i = 0; i < row; i++) {

						Cell cell = sheet.getCell(j, i);

						TestCase[i][j] = cell.getContents();
					}
				}

				int iInput = 3, iOutput = 4;
				// Run test case for number of iteration
				TestSuite: for (int iLoop2 = 1; iLoop2 <= Integer
						.parseInt(sIteration[iLoop1]); iLoop2++) {

					setup();

					iStepCount = 0;

					iInput = iInput + 2;

					iOutput = iOutput + 2;

					// load test case steps in array
					for (int k = 0; k < row; k++) {

						tComment[iStepCount] = TestCase[k][0];

						tAction[iStepCount] = TestCase[k][1];

						tLabelName[iStepCount] = TestCase[k][2];

						tObjectName[iStepCount] = TestCase[k][3];

						tParent_Object[iStepCount] = TestCase[k][4];

						tInputData[iStepCount] = TestCase[k][iInput];

						tExpectedData[iStepCount] = TestCase[k][iOutput];

						debug("Test Step :- " + "Comment : "
								+ tComment[iStepCount] + "Action : "
								+ tAction[iStepCount] + "Label : "
								+ tLabelName[iStepCount] + "Object Name : "
								+ tObjectName[iStepCount] + "Page : "
								+ tParent_Object[iStepCount] + "Input Data : "
								+ tInputData[iStepCount] + "Output : "
								+ tExpectedData[iStepCount] + "Step Count : "
								+ iStepCount);

						iStepCount = iStepCount + 1;

					}

					// create table header
					CreateTableHeader(sTestCase_Name[iLoop1],
							sFunctional_Area[iLoop1], iLoop2);

					debug("Test Case Execution: - Test Case ID : "
							+ sTestCase_ID[iLoop1] + " , Execute : "
							+ sExecute[iLoop1] + " , Test Case Name : "
							+ sTestCase_Name[iLoop1] + " , Iteration : "
							+ iLoop2 + " , Functional Area : "
							+ sFunctional_Area[iLoop1]);

					// this for loop executes each step of test case
					for (int iLoop3 = 1; iLoop3 < iStepCount; iLoop3++) {

						info("Inside Loop2 to execute test cases");

						if (tAction[iLoop3] != null) {

							debug("Test Step Execution:- " + "Action : "
									+ tAction[iLoop3] + "Label : "
									+ tLabelName[iLoop3] + "Object Name : "
									+ tObjectName[iLoop3] + "Page : "
									+ tParent_Object[iLoop3] + "Input Data : "
									+ tInputData[iLoop3] + "Output : "
									+ tExpectedData[iLoop3] + ", Step No : "
									+ iLoop3);

							long starttime = System.currentTimeMillis();

							boolean status = KeywordGenerator(tAction[iLoop3],
									tLabelName[iLoop3], tObjectName[iLoop3],
									tParent_Object[iLoop3], tInputData[iLoop3],
									tExpectedData[iLoop3]);

							long endtime = System.currentTimeMillis();

							long temp = endtime - starttime;

							TestExecutionTime = String.format("%d:%02d:%02d",
									(temp / (1000 * 60 * 60)) % 24,
									(temp / (1000 * 60)) % 60,
									(temp / 1000) % 60);

							if (status == false) {

								continue TestSuite;

							}
						} else {

							error("Action Field is blank");
						}

					} // For iLoop3
					CreateTableFooter(iLoop2);

					tearDown();
				}// For iLoop2
			} // For iLoop1

			CloseReport();

		} // Try
		catch (Exception e) {

			error(e);

			CloseReport();
		}
	}

	// ************************************************************************************************************
	// *** Implementation of IUtility Interface ***
	// ************************************************************************************************************
	/**
	 * Purpose : This function checks that Object is displayed for time
	 * specified in Timeout constant
	 * 
	 * @return
	 */
	public boolean isObjectDisplayed() {
		info("isObjectDisplayed() Method Starts..");
		boolean exist = false;
		try {
			for (int second = 0; second < Timeout; second++) {
				driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
				if (element.isDisplayed()) {
					exist = true;
					break;
				}
			}
		} catch (NoSuchElementException e) {
			error(e);
			exist = false;
		}
		info("isObjectDisplayed() Method Ends..");
		return exist;
	}

	/**
	 * Purpose : This function checks that Object is enabled
	 * 
	 * @return
	 */
	public boolean isObjectEnabled() {
		info("isObjectEnabled() Method Starts..");
		boolean exist = false;
		try {
			for (int second = 0; second < Timeout; second++) {
				driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
				if (element.isEnabled()) {
					exist = true;
					break;
				}
			}
		} catch (NoSuchElementException e) {
			error(e);
			exist = false;
		}
		info("isObjectEnabled() Method Ends..");
		return exist;
	}

	/**
	 * Purpose : This function compares two strings, returns true/false
	 * 
	 * @param fromApp
	 * @param tOutput
	 * @return
	 */
	public boolean StringCompare(String fromApp, String tOutput) {
		boolean temp = false;
		if (fromApp.equals(tOutput)) {
			temp = true;
		} else {
			temp = false;
		}
		return temp;
	}

	/**
	 * Purpose : Create reference to Browser Object & launch URL
	 */
	public void launchBrowser() {
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
		driver.get("http://timesofIndia.com");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
	}

	/**
	 * Purpose : This function runs before execution of each Test Script & does
	 * initial setup for automation suite
	 */
	public void setup() {
		try {
			if (driver.findElement(By.name("name")).isDisplayed()) {
				driver.findElement(By.name("name")).sendKeys("abhay");
				driver.findElement(By.name("pass")).sendKeys("test12");
				driver.findElement(By.name("op")).click();
				WebDriverWait wait = new WebDriverWait(driver, 10);
				wait.until(
						ExpectedConditions.presenceOfElementLocated(By
								.linkText("Log out"))).isDisplayed();
			} else {
				File file = new File(System.getProperty("user.dir")
						+ "\\Core-Jars\\IEDriverServer.exe");
				System.setProperty("webdriver.ie.driver",
						file.getAbsolutePath());
				launchBrowser();
			}
		} catch (Exception e) {
			info("Launching Browser");
			launchBrowser();
		}
	}

	/**
	 * Purpose : This function runs after execution of Test Script & perform
	 * tear down acitivity
	 */
	public void tearDown() {
		try {
			if (driver.findElement(By.linkText("Log out")).isDisplayed()) {
				driver.findElement(By.linkText("Log out")).click();
			} else {
				driver.quit();
			}
		} catch (NoSuchElementException e) {
			driver.quit();
		}
	}

	/**
	 * Purpose : returns Machine current Time
	 * 
	 * @return
	 */
	public String GetTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		String strTime = sdfTime.format(now);
		return strTime;
	}

	/**
	 * Purpose : returns Machine current Date
	 * 
	 * @return
	 */
	public String GetDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		strDate = strDate.replace("-", "/");
		return strDate;
	}

	// ************************************************************************************************************
	// *** Implementation of IConfig Interface ***
	// ************************************************************************************************************

	// ************************************************************************************************************
	// *** Implementation of IKeyword Interface ***
	// ************************************************************************************************************
	public static enum Keyword_Name {
		LAUNCHED, SET, EXIST, CLICK, SELECT, VERIFYTEXT, DOUBLECLICK, LIST, DESELECT, VERIFYTITLE, OPENPOPUP, CLOSEPOPUP, ALERT, SWITCHFRAME, CLOSEFRAME, VERIFYURL, CLEAR, GETTEXT, VERIFYFONTSIZE, VERIFYFONTFAMILY, ISSELECTED, VERIFYCOMBO, GETSELECTEDTEXT
	}

	/**
	 * Purpose : This function implements keyword which peforms operation on AUT
	 */
	public boolean KeywordGenerator(String tAction, String tLabelname,
			String tObject, String tParent, String tInput, String tOutput) {

		if (tObject.isEmpty()) {
		} else {
			ORGenerator(tObject);
		}
		Keyword_Name kAction = Keyword_Name.valueOf(tAction);
		if (kAction != null) {
			switch (kAction) {
			// -----------------Launched - invoke browser
			case LAUNCHED:

				info("Keyword : LAUNCHED Starts..");
				try {
					info("Inside Try block..");

					strDesc = tLabelname + " is displayed and " + "is launched";

					Pass(tAction, tParent, tObject, "", strDesc);

				} catch (Exception e) {

					error("Keyword : LAUNCHED " + e);

					Fail(tAction, tParent, tObject, "", strDesc);

				}

				info("Keyword : LAUNCHED Ends..");

				break;

			// -----------------SET - enter value in Edit box
			case SET:

				info("Keyword : SET Starts..");

				try {

					element.sendKeys(tInput);

					callPassDebug(tAction, tParent, tObject, tInput, tLabelname
							+ " : " + tInput + " is entered");

				} catch (NoSuchElementException e) {

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ tObject + " : " + "is not found, ", tObject + e);

				}

				info("Keyword : SET Ends..");
				break;
			// ----------------- CLEAR - Clears text from Text Input field
			case CLEAR:
				info("Keyword : CLEAR Starts..");

				try {

					element.clear();

					callPassDebug(tAction, tParent, tObject, tInput, tLabelname
							+ " : " + tInput + " is entered");

				} catch (Throwable e) {

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ tObject + " : " + "is not found, ", tObject + e);

				}

				info("Keyword : CLEAR Ends..");
				break;
			// ----------------- GETTEXT - Returns text from WebElement
			case GETTEXT:
				info("Keyword : GETTEXT Starts..");
				info("Keyword : GETTEXT Ends..");
				break;
			// ----------------- VERIFYFONTFAMILY - Returns text from WebElement
			case VERIFYFONTFAMILY:
				info("Keyword : VERIFYFONTFAMILY Starts..");
				info("Keyword : VERIFYFONTFAMILY Ends..");
				break;
			// ----------------- VERIFYFONTSIZE - Returns text from WebElement
			case VERIFYFONTSIZE:
				info("Keyword : VERIFYFONTSIZE Starts..");
				info("Keyword : VERIFYFONTSIZE Ends..");
				break;
			// ----------------- ISSELECTED - Returns Radio/Checkbox current
			case ISSELECTED:
				info("Keyword : ISSELECTED Starts..");
				info("Keyword : ISSELECTED Ends..");
				break;
			// ----------------- GETSELECTEDTEXT - Returns selected value of
			case GETSELECTEDTEXT:
				info("Keyword : GETSELECTEDTEXT Starts..");
				info("Keyword : GETSELECTEDTEXT Ends..");
				break;
			// ----------------- VERIFYCOMBO - Checks value in Combo/List
			case VERIFYCOMBO:
				info("Keyword : VERIFYCOMBO Starts..");
				info("Keyword : VERIFYCOMBO Ends..");
				break;
			// ----------------- CLICK - Perform Mouse Click on WebElement
			case CLICK:

				try {

					element.click();

					callPassDebug(tAction, tParent, tObject, tInput, tLabelname
							+ " : " + tInput + " is Clicked");

				} catch (Exception e) {

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ tObject + " : " + "is not found, ", tObject + e);

				}

				break;

			// -----------------EXIST - To Check Object is displayed
			case EXIST:

				info("Keyword : EXIST Starts..");

				debug("Input Value : " + tOutput);

				if (tOutput.isEmpty() || tObject.isEmpty()) {

					callFailError(
							tAction,
							tParent,
							tObject,
							tInput,
							tLabelname
									+ tObject
									+ " : "
									+ "Either Object Name or Expected parameter is not provided, ",
							tObject
									+ " Either Object Name or Expected parameter is not provided");

					break;
				}

				boolean temp = false;

				if (tOutput.equalsIgnoreCase("true") == true) {
					temp = true;
				}

				if (tOutput.equalsIgnoreCase("false") == true) {
					temp = false;
				}

				try {
					strDesc = tLabelname + " : " + "is displayed, Expected : "
							+ tOutput + " , Actual : " + temp;

					callPassDebug(tAction, tParent, tObject, tInput, strDesc);

				} catch (NoSuchElementException e) {

					strDesc = tLabelname + " : "
							+ "is not displayed , Expected : " + tOutput
							+ " , Actual : " + temp;

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, tObject + e);

				}

				info("Keyword : EXIST Ends..");

				break;

			// ----------------- VERIFYTEXT - Check Text/Label/Caption Displayed
			case VERIFYTEXT:

				info("Keyword : VERIFYTEXT Starts..");

				debug("Input Value : " + tOutput);

				try {

					if (StringCompare(element.getText(), tOutput)) {

						strDesc = tLabelname + " : " + element.getText()
								+ " , " + tOutput + "Text is matched";

						callPassDebug(tAction, tParent, tObject, tInput,
								strDesc);

					} else {

						strDesc = tLabelname + " : " + element.getText()
								+ " , " + tOutput + "Text is not matched";

						callFailError(tAction, tParent, tObject, tInput,
								tLabelname + strDesc, tObject
										+ "Text is not matched");

					}

				} catch (NoSuchElementException e) {

					strDesc = tObject + " : " + "is not found, ";

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, tObject + e);

				}

				info("Keyword : VERIFYTEXT Ends..");

				break;

			// ----------------- DOUBLECLICK - Perform Mouse Double Click
			case DOUBLECLICK:

				info("Keyword : DOUBLECLICK Starts..");

				debug("Input Value : " + tOutput);

				try {

					Actions builder = new Actions(driver);

					builder.doubleClick(element).build().perform();

					strDesc = tLabelname
							+ " double click operation is performed";

					callPassDebug(tAction, tParent, tObject, tInput, strDesc);

				} catch (NoSuchElementException e) {

					strDesc = tLabelname + " : " + "is not displayed";

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, tObject + e);

				}

				info("Keyword : DOUBLECLICK Ends..");

				break;

			// ----------------- SELECCT - To Select Single Radio/Checkbox
			case SELECT:

				info("Keyword : SELECT Starts..");

				debug("Input Value : " + tInput);

				try {

					if (!element.isSelected()) {

						element.click();

						strDesc = tLabelname + " : is selected";

						callPassDebug(tAction, tParent, tObject, tInput,
								strDesc);

					}

				} catch (NoSuchElementException e) {

					strDesc = tLabelname + " : " + "is not displayed";

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, tObject + e);

				}

				info("Keyword : SELECT Ends..");

				break;

			// ----------------- DESELECT - To Deselect Single Radio/Checkbox
			case DESELECT:

				info("Keyword : DESELECT Starts..");

				debug("Input Value : " + tInput);

				try {

					if (element.isSelected()) {

						element.click();

						strDesc = tLabelname + " : is deselected";

						callPassDebug(tAction, tParent, tObject, tInput,
								strDesc);

					}

				} catch (NoSuchElementException e) {

					strDesc = tLabelname + " : " + "is not displayed";

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, tObject + e);

				}

				info("Keyword : DESELECT Ends..");

				break;
			// ----------------- LIST - To Select Value in Combo Box/List
			case LIST:

				info("Keyword : LIST Starts..");

				debug("Input Value : " + tInput);

				try {
					element = driver.findElement(bProperty);

					if (isObjectDisplayed() == true) {

						Select make = new Select(element);

						make.selectByVisibleText(tInput);

						if (StringCompare(make.getFirstSelectedOption()
								.getText(), tInput))
							;

						strDesc = tLabelname + " : " + tInput + " is selected";

						callPassDebug(tAction, tParent, tObject, tInput,
								strDesc);

					} else {

						strDesc = tLabelname + " : " + tInput
								+ " is not selected";

						callFailError(tAction, tParent, tObject, tInput,
								tLabelname + strDesc, tObject + strDesc);
					}

				} catch (NoSuchElementException e) {

					strDesc = tLabelname + " : " + "is not displayed";

					Fail(tAction, tParent, tObject, tOutput, strDesc);

					error(tObject + " is not displayed");

				}

				info("Keyword : LIST Ends..");

				break;
			// ----------------- OPENPOPUP - To Open Child/Pop up/ Second Window
			case OPENPOPUP:
				info("Keyword : OPENPOPUP Starts..");

				// TO DO

				info("Keyword : OPENPOPUP Ends..");

				break;

			// ----------------- CLOSEPOPUP - To Close Child/Pop up/ Second
			case CLOSEPOPUP:
				info("Keyword : OPENPOPUP Starts..");

				// TO DO

				info("Keyword : OPENPOPUP Ends..");

				break;

			// ----------------- VERIFYTITLE - To Verify Title of Browser
			case VERIFYTITLE:
				info("Keyword : VERIFYTITLE Starts..");

				String ltitle = driver.getTitle();

				boolean lresult = StringCompare(tOutput, ltitle);

				if (lresult) {

					strDesc = tLabelname + " : Expected Title : " + tOutput
							+ " Actual Title : " + ltitle + " is matched";

					callPassDebug(tAction, tParent, tObject, tInput, strDesc);

				} else {

					strDesc = tLabelname + " : Expected Title : " + tOutput
							+ " Actual Title : " + ltitle + " is not matched";

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, strDesc);

				}

				info("Keyword : VERIFYTITLE Ends..");

				break;
			// ----------------- VERIFYTITLE - To Verify Title of Browser
			case VERIFYURL:

				info("Keyword : VERIFYURL Starts..");

				String turl = driver.getCurrentUrl();

				boolean strresult = StringCompare(tOutput, turl);

				if (strresult) {

					strDesc = tLabelname + " : Expected URL: " + tOutput
							+ " Actual URL: " + turl + " is matched";

					callPassDebug(tAction, tParent, tObject, tInput, strDesc);

				} else {

					strDesc = tLabelname + " : Expected URL: " + tOutput
							+ " Actual URL: " + turl + " is not matched";

					callFailError(tAction, tParent, tObject, tInput, tLabelname
							+ strDesc, tObject + strDesc);

				}

				info("Keyword : VERIFYURL Ends..");

				break;
			// ----------------- ALERT - To VERIFY Title & Close ALERT
			case ALERT:
				info("Keyword : OPENPOPUP Starts..");

				// TO DO

				info("Keyword : OPENPOPUP Ends..");

				break;
			// ----------------- SWITCHFRAME - To Set On IFrame
			case SWITCHFRAME:
				info("Keyword : SWITCHFRAME Starts..");

				// TO DO

				info("Keyword : SWITCHFRAME Ends..");

				break;
			// ----------------- CLOSEFRAME - To Close IFrame
			case CLOSEFRAME:
				info("Keyword : CLOSEFRAME Starts..");
				// TO DO
				info("Keyword : CLOSEFRAME Ends..");
				break;

			// ----------------- some text------------------------------
			default:
				strDesc = tAction + " keyword not found";

				callFailError(tAction, tParent, tObject, tInput, tLabelname
						+ strDesc, strDesc);

				break;

			}// Closing the switch
		} else {

			error("Keyword field is blank");

		}// closing if

		debug("Test Case Status :- " + TestCaseStatus);

		return TestCaseStatus;
	} // closing function

	/**
	 * Purpose : This function returns Reference of WebElement
	 * 
	 * @param tObject
	 */
	public void ORGenerator(String tObject) {

		info("Method ORGenerator Starts..");

		try {

			Workbook w;

			int trownumber;

			String tPropertyType = null, tPropertyvalue = null;

			w = Workbook.getWorkbook(inputWorkbook);

			Sheet sheet = w.getSheet("OR");

			// Read object property from EXCEL OR sheet
			if (sheet.findLabelCell(tObject) != null) {

				trownumber = (int) sheet.findLabelCell(tObject).getRow();

				tPropertyType = sheet.getCell(1, trownumber).getContents();

				tPropertyvalue = sheet.getCell(2, trownumber).getContents();

				debug("Property Name: -" + tPropertyType + "Property value: -"
						+ tPropertyvalue);
			}

			// Assigning values to appropriate locator & returning reference to
			// bProperty variable
			if (tPropertyType.equalsIgnoreCase("css")) {

				bProperty = By.cssSelector(tPropertyvalue);
			}

			else if (tPropertyType.equalsIgnoreCase("id")) {

				bProperty = By.id(tPropertyvalue);
			}

			else if (tPropertyType.equalsIgnoreCase("linkText")) {

				bProperty = By.linkText(tPropertyvalue);
			}

			else if (tPropertyType.equalsIgnoreCase("name")) {

				bProperty = By.name(tPropertyvalue);

			}

			else if (tPropertyType.equalsIgnoreCase("partialLinkText")) {

				bProperty = By.partialLinkText(tPropertyvalue);

			}

			else if (tPropertyType.equalsIgnoreCase("tagName")) {

				bProperty = By.tagName(tPropertyvalue);
			}

			else if (tPropertyType.equalsIgnoreCase("xpath")) {

				bProperty = By.xpath(tPropertyvalue);
			}

			else if (tPropertyType.equalsIgnoreCase("link")) {

				bProperty = By.linkText(tPropertyvalue);
			}

			element = driver.findElement(bProperty);

			if ((isObjectDisplayed() == true) || (isObjectEnabled() == true)) {

				info(tObject + " is displayed");

			}

			else {

				error(tObject + " is not displayed");

				Fail("", "", "", "", tObject + " is not displayed");
			}

		} catch (Exception e) {

			error("ORGenerator Method .. " + e);

		}
		info("ORGenerator Method Ends..");
	} // closing brace for ORGenerator function

	// ************************************************************************************************************
	// *** Implementation of IReport Interface ***
	// ************************************************************************************************************

	/**
	 * Purpose : GET CURRENT SYSTEM DATE & TIME
	 */
	public void GetDateTime() {
		try {

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");

			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

			Date now = new Date();

			String strDate = sdfDate.format(now);

			String strTime = sdfTime.format(now);

			strTime = strTime.replace(":", "-");

			sDateTime = strDate + "_" + strTime;
		}

		catch (Exception e) {

			error(e);
		}
	}

	/**
	 * Purpose : CREATE REPORT FILE
	 */
	public void CreateReport() {
		info("CreateReport method starts..");
		GetDateTime();
		sFilename = "Execution_Report_" + sDateTime + ".html";
		try {

			htmlreport = System.getProperty("user.dir")
					+ "\\src\\com\\report\\Runtime_Report" + File.separator
					+ sFilename;

			file = new File(htmlreport);

			output = new BufferedWriter(new FileWriter(file));

			output.write("<html><body>");

			output.write("<align =middle><h2><font face=Georgia <u>AUTOMATED TEST EXECUTION REPORT</u></h2></font>");

			output.write("<table cellpadding=0 cellspacing=0 width=1000 border=1 bordercolor=BLACK>");

			output.write("<tr>");

			output.write("<td width=150 bgcolor=#0404B4 align=middle><font face=Georgia size=2 color=white><b>Action</b></font></td>");

			output.write("<td width=100 bgcolor=#0404B4 align=middle><font face=Georgia size=2 color=white><b>Page</b></font></td>");

			output.write("<td width=150 bgcolor=#0404B4 align=middle><font face=Georgia size=2 color=white><b>Object</b></font></td>");

			output.write("<td width=150 bgcolor=#0404B4 align=middle><font face=Georgia size=2 color=white><b>Test Data</b></font></td>");

			output.write("<td width=150 bgcolor=#0404B4 align=middle><font face=Georgia size=2 color=white><b>Verification</b></font></td>");

			output.write("<td width=150 bgcolor=#0404B4 align=middle><font face=Georgia size=2 color=white><b>Result</b></font></td>");

			output.write("</tr>");

		} catch (Exception e) {

			error("CreateReport() Method .." + e);
		}

		info("CreateReport() method ends..");
	}

	/**
	 * Purpose : CLOSE REPORT FILE
	 */
	public void CloseReport() {
		info("CloseReport() method starts..");

		try {

			output.close();
		} catch (Exception e) {

			error(e);
		}
		info("CloseReport() method ends..");
	}

	/**
	 * Purpose : CREATE TABLE HEADER OF REPORT FILE
	 */
	public void CreateTableHeader(String sTestScenario, String sTestStep,
			int sIteration) {
		info("CreateTableHeader() method starts..");

		info("Input Parameter , sTestScenario : " + sTestScenario
				+ " ,sTestStep : " + sTestStep);

		GetDateTime();

		try {

			output.write("<tr>");

			output.write("<td colspan=6 width=1000 bgcolor=#EFFBF5  align= Left><font face = Georgia size = 2><b>Test Case Name ::   </b></font> <font face = Georgia size=2>"
					+ sTestScenario + "</font></br>");

			output.write("<b>Scenarion Name ::   </b></font> <font face = Georgia size=2>"
					+ sTestStep + "</font></br>");

			output.write("Iteration  ::   </font> <font face = Georgia size=2>"
					+ sIteration + "</font></br>");

			output.write("Execution Start Time  ::   </font> <font face = Georgia size=2>"
					+ GetDate() + " " + GetTime() + "</font></br></td>");

			output.write("</tr>");

		} catch (Exception e) {

			error("CreateTableHeader() - Inside Exception.. " + e);
		}
		info("CreateTableHeader() method ends..");
	}

	/**
	 * Purpose : CREATE TEST Case Footer OF REPORT FILE
	 * 
	 * @param sIteration
	 */
	public void CreateTableFooter(int sIteration) {
		info("CreateTableFooter() method starts..");

		debug("Iteration : " + sIteration);
		GetDateTime();

		try {

			output.write("<tr>");

			output.write("<td colspan=6 width=1000 bgcolor=#EFFBF5  align= Left><font face = Georgia size = 2>Iteration</b></font> <font face = Georgia size=2>"
					+ sIteration + " Execution Completed </font></br>");

			output.write("<b>Exeuction Time ::   </b></font> <font face = Georgia size=2>"
					+ ((TestExecutionTime)) + "</font></br>");

			output.write("Execution completed at  ::   </font> <font face = Georgia size=2>"
					+ GetDate() + " " + GetTime() + "</font></td>");

			output.write("</tr>");

		} catch (Exception e) {

			error(e);
		}

		info("CreateTableFooter() method ends..");
	}

	/**
	 * Purpose : WRITE TEST CASE PASS RESULT IN REPORT FILE
	 * 
	 * @param sAction
	 *            , sWindow, sObject, sTestData, sVerificationStep
	 */
	public void Pass(String sAction, String sWindow, String sObject,
			String sTestData, String sVerificationStep) {
		info("Pass method starts..");
		debug(sAction + "," + sWindow + "," + sObject + "," + sTestData + ","
				+ sVerificationStep);
		try {
			output.write("<tr>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=black>"
					+ sAction + "</font></td>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=black>"
					+ sWindow + "</font></td>");

			output.write("<td width=150 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=black>"
					+ sObject + "</font></td>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=black>"
					+ sTestData + "</font></td>");

			output.write("<td width=400 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=black>"
					+ sVerificationStep + "</font></td>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=green><b>PASS</b></font></td>");

			output.write("</tr>");

		} catch (Exception e) {

			debug("Pass method -- Inside Exception");

			error(e);
		}

		info("Pass method ends..");
	}

	/**
	 * Purpose : WRITE TEST CASE FAIL RESULT IN REPORT FILE
	 * 
	 * @param : sAction, sWindow, sObject, sTestData, VerificationStep
	 */
	public void Fail(String sAction, String sWindow, String sObject,
			String sTestData, String sVerificationStep) {
		info("Fail method starts..");

		debug(sAction + "," + sWindow + "," + sObject + "," + sTestData + ","
				+ sVerificationStep);

		String error = Snap_Shots(driver);

		try {
			output.write("<tr>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=red>"
					+ sAction + "</font></td>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=red>"
					+ sWindow + "</font></td>");

			output.write("<td width=150 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=red>"
					+ sObject + "</font></td>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=red>"
					+ sTestData + "</font></td>");

			output.write("<td width=400 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=red>"
					+ sVerificationStep
					+ "<a href="
					+ error
					+ ">"
					+ " Click to Open Screenshot of Error</a></font></td>");

			output.write("<td width=100 bgcolor=#EFFBF5  align=left><font face=Georgia size=2 color=red><b>FAIL !</b></font></td>");

			output.write("</tr>");
		}

		catch (Exception e) {
			System.err.println(e);
		}

		info("Fail method ends..");
	}

	/**
	 * Purpose : Takes screenshot of error & returns file path
	 * 
	 * @param driver
	 */
	public String Snap_Shots(WebDriver driver) {
		info("SnapShots method starts..");

		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);

		GetDateTime();

		screenshotpath = System.getProperty("user.dir")
				+ "\\src\\com\\report\\Error\\" + sDateTime + ".png";

		try {

			FileUtils.copyFile(scrFile, new File(screenshotpath));

		} catch (IOException e) {

			e.printStackTrace();
		}
		info("Snap_Shots method ends..");

		return screenshotpath;

	}

	/**
	 * Purpose : function to open html report at the end of execution
	 */
	public void openReport() {

		if (Desktop.isDesktopSupported()) {
			try {

				File htmlfile = new File(htmlreport);

				Desktop.getDesktop().open(htmlfile);

			} catch (IOException ex) {

				System.out.println("Error opening a html page.");

				ex.printStackTrace();
			}
		}
	}

	// ************************************************************************************************************
	// *** Implementation of ILog Interface ***
	// ************************************************************************************************************
	/**
	 * Purpose : function to write debug message in execution log file
	 * 
	 * @param logstr
	 */
	public static void debug(String logstr) {
		log.debug("Debug : " + logstr);
	}

	/**
	 * Purpose : function to write info message in execution log file
	 * 
	 * @param logstr
	 */
	public void info(String logstr) {
		log.info("Info : " + logstr);
	}

	/**
	 * Purpose : function to write warning message in execution log file
	 * 
	 * @param logstr
	 */
	public void warn(String logstr) {
		log.warn("Warn : " + logstr);

	}

	/**
	 * Purpose : function to write System generated exception in execution log
	 * file
	 * 
	 * @param logstr
	 */
	public void error(Exception logstr) {
		log.error("Exception Error : " + logstr);
	}

	/**
	 * Purpose : function to write error message in execution log file
	 * 
	 * @param logstr
	 */
	public void error(String logstr) {

		log.error("Error : " + logstr);

	}

	/**
	 * Purpose : function to write fail status in HTML report
	 * 
	 * @param tAction
	 *            , tParent, tObject, tInput, strDesc, strError
	 */
	public void callFailError(String tAction, String tParent, String tObject,
			String tInput, String strDesc, String strError) {
		error("Keyword : " + tAction + " : " + strError);
		Fail(tAction, tParent, tObject, tInput, strDesc);
	}

	/**
	 * Purpose : function to write Pass status in HTML report
	 * 
	 * @param tAction
	 *            , tParent, tObject, tInput, strDesc, strError
	 */
	public void callPassDebug(String tAction, String tParent, String tObject,
			String tInput, String strDesc) {

		debug("Keyword : " + tAction + " : Browser :" + tParent + "Object :"
				+ tObject + "Input :" + tInput + " , " + strDesc);

		Pass(tAction, tParent, tObject, tInput, strDesc);
	}

	/**
	 * Purpose : Function to Start execution
	 */
	public void startExecution() {
		info("______________________________________________________________");

		info("startExecution method starts..");

		GetSuite();

		GetTestCase();

		openReport();

		info("startExecution method ends..");

	}
}
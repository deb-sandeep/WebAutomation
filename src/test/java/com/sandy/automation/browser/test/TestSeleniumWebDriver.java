package com.sandy.automation.browser.test;

import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.chrome.ChromeDriver ;

public class TestSeleniumWebDriver {
    
    private static final Logger log = Logger.getLogger( TestSeleniumWebDriver.class ) ;
    
    private static final String EXPORT_BTN_ID = "ctl00_ContentPlaceHolder1_btnExportToExcel" ;
    private static final String MF_PAGE_LINKS_PATH = ".mfshortnames_links_wrapper a[href^='/funds']" ;

    private WebDriver webDriver = null ;
    
    public TestSeleniumWebDriver() {
        webDriver = new ChromeDriver() ;
    }
    
    public void execute() {
        
        List<WebElement> elements = null ;
        
        try {
            webDriver.get( "https://www.morningstar.in/default.aspx" ) ;
            elements = webDriver.findElements( By.cssSelector( MF_PAGE_LINKS_PATH  ) ) ;

            Map<String, String> pageLinks = new LinkedHashMap<>() ;
            
            for( WebElement element : elements ) {
                String pageURL = element.getAttribute( "href" ) ;
                String fundName = element.getText() ;
                pageLinks.put( fundName, pageURL ) ;
            }
            
            for( String key : pageLinks.keySet() ) {
                String url = pageLinks.get( key ) ;
                log.debug( "Processing " + key ) ;
                log.debug( "\t@ " + url ) ;
                processMFPageURL( url ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error in automation.", e ) ;
        }
        finally {
            webDriver.quit() ;
        }
    }
    
    private void processMFPageURL( String url ) 
        throws Exception {
        
        log.debug( "\t\tLoading..." ) ;
        webDriver.get( url ) ;
        WebElement exportBtn = webDriver.findElement( By.id( EXPORT_BTN_ID  ) ) ;
        log.debug( "\t\tDownloading xls..." ) ;
        exportBtn.click() ;
        Thread.sleep( 2000 ) ;
    }
    
    public static void main( String[] args ) {

        TestSeleniumWebDriver driver = new TestSeleniumWebDriver() ;
        driver.execute() ;
    }
}

package com.sandy.automation.browser.mfmetaenrich;

import java.io.File ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automation.browser.AutomationBase ;
import com.sandy.automation.util.ConfigUtils ;

/**
 * This Selenium WebDriver automation fetches mutual fund meta information
 * from morningstar site and calls on the Capitalyst API to enrich the 
 * meta repository.
 */
public class AutoEnrichMFMeta extends AutomationBase {
    
    private static final Logger log = Logger.getLogger( AutoEnrichMFMeta.class ) ;
    
    private static final String SITE_URL = "https://www.morningstar.in/default.aspx" ;
    private static final String EXPORT_BTN_ID = "ctl00_ContentPlaceHolder1_btnExportToExcel" ;
    private static final String MF_FUND_NAME_ID = "ctl00_ContentPlaceHolder1_lblHeader" ;
    private static final String MF_PAGE_LINKS_PATH = ".mfshortnames_links_wrapper a[href^='/funds']" ;

    private String capitalystServerAddress = null ;
    
    public AutoEnrichMFMeta() throws Exception {
        capitalystServerAddress = config.getString( "capitalystServer.address", 
                                                    "localhost:8080" ) ;
        log.debug( "Using capitalyst server @ " + capitalystServerAddress ) ;
    }
    
    @Override
    public PropertiesConfiguration loadAppConfig() throws Exception {
        return ConfigUtils.loadPropertiesConfig( "mf-enrich" ) ;
    }
    
    public void execute() {
        
        List<WebElement> elements = null ;
        
        try {
            super.initializeWebDriver() ;
            webDriver.get( SITE_URL ) ;
            elements = webDriver.findElements( By.cssSelector( MF_PAGE_LINKS_PATH  ) ) ;

            Map<String, String> pageLinks = new LinkedHashMap<>() ;
            
            for( WebElement element : elements ) {
                String pageURL = element.getAttribute( "href" ) ;
                String fundName = element.getText() ;
                pageLinks.put( fundName, pageURL ) ;
            }
            
            for( String fundGroupId : pageLinks.keySet() ) {
                String url = pageLinks.get( fundGroupId ) ;
                log.debug( "Processing " + fundGroupId ) ;
                log.debug( "\t@ " + url ) ;
                processMFPageURL( fundGroupId, url ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error in automation.", e ) ;
        }
        finally {
            webDriver.quit() ;
        }
    }
    
    private void processMFPageURL( String fundGroupId, String url ) 
        throws Exception {
        
        cleanDownloadsFolder() ;

        log.debug( "\t\tLoading..." ) ;
        webDriver.get( url ) ;
        
        WebElement fundNameHeader = webDriver.findElement( By.id( MF_FUND_NAME_ID ) ) ;
        String fundMgmtCompanyName = fundNameHeader.getText() ;
        log.debug( "\t\tFund name = " + fundMgmtCompanyName ) ;
        
        File csvFile = super.downloadFile( By.id( EXPORT_BTN_ID  ) ) ;
        processDownloadedFile( fundGroupId, fundMgmtCompanyName, csvFile ) ;
    }
    
    private void processDownloadedFile( String groupId, 
                                        String coName,
                                        File csvFile ) throws Exception {
        
        DownloadedFileProcessor fileProcessor = null ;
        fileProcessor = new DownloadedFileProcessor( groupId, coName, 
                                                     csvFile, 
                                                     capitalystServerAddress ) ;
        fileProcessor.execute() ;
    }
    
    public static void main( String[] args ) throws Exception {
        log.debug( "Starting Enrich MF Meta automation..." ) ;
        AutoEnrichMFMeta driver = new AutoEnrichMFMeta() ;
        driver.execute() ;
    }
}

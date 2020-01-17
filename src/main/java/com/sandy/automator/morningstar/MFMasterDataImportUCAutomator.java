package com.sandy.automator.morningstar;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.File ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class MFMasterDataImportUCAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( MFMasterDataImportUCAutomator.class ) ;
    
    private static final String SITE_URL = "https://www.morningstar.in/default.aspx" ;
    private static final String EXPORT_BTN_ID = "ctl00_ContentPlaceHolder1_btnExportToExcel" ;
    private static final String MF_FUND_NAME_ID = "ctl00_ContentPlaceHolder1_lblHeader" ;
    private static final String MF_PAGE_LINKS_PATH = ".mfshortnames_links_wrapper a[href^='/funds']" ;

    private Browser browser = null ;
    private String serverAddress = null ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        List<WebElement> elements = null ;
        
        browser.get( SITE_URL ) ;
        elements = browser.findElements( By.cssSelector( MF_PAGE_LINKS_PATH  ) ) ;

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
            break ;
        }
    }

    private void processMFPageURL( String fundGroupId, String url ) 
            throws Exception {
            
        browser.cleanDownloadsFolder() ;

        log.debug( "\t\tLoading..." ) ;
        browser.get( url ) ;
        
        WebElement fundNameHeader = browser.findElement( By.id( MF_FUND_NAME_ID ) ) ;
        String fundMgmtCompanyName = fundNameHeader.getText() ;
        log.debug( "\t\tFund name = " + fundMgmtCompanyName ) ;
        
        File csvFile = browser.downloadFile( By.id( EXPORT_BTN_ID  ) ) ;
        processDownloadedFile( fundGroupId, fundMgmtCompanyName, csvFile ) ;
    }
        
    private void processDownloadedFile( String groupId, 
                                        String coName,
                                        File csvFile ) throws Exception {
        
        DownloadedFileProcessor fileProcessor = null ;
        fileProcessor = new DownloadedFileProcessor( groupId, coName, csvFile ) ; 

        List<String[]> records = fileProcessor.execute() ;
        if( records != null && !records.isEmpty() ) {
            browser.postDataToServer( serverAddress, 
                                      "/MutualFund/EnrichMFMeta", 
                                      records ) ; 
        }
    }
}

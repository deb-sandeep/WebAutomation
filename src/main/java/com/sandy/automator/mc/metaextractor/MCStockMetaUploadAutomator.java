package com.sandy.automator.mc.metaextractor ;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.ObjectSerializer ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class MCStockMetaUploadAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( MCStockMetaUploadAutomator.class ) ;
    
    private static final String SER_FILE_NAME = "MCMetaMap" ;
    
    private static final String BASE_URL = 
            "https://www.moneycontrol.com/markets/indian-indices/" ;
    
    static final String NIFTY_200_URL = BASE_URL + 
            "top-nse-200-companies-list/49?classic=true&categoryId=1&exType=N" ;
    
    static final String NIFTY_500_URL = BASE_URL + 
            "top-nse-500-companies-list/7?classic=true&categoryId=1&exType=N" ;

    private Browser browser = null ;
    private ObjectSerializer serializer = new ObjectSerializer() ;
    
    private String serverAddress = null ;
    
    private Map<String, String> stockDetailPageLinks = new LinkedHashMap<>() ;
    private Map<String, MCStockMeta> stockMetaMap = null ;
    
    @SuppressWarnings( "unchecked" )
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        log.debug( "Executing MCStockMetaUploadAutomator" ) ;
        this.browser = browser ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        stockMetaMap = (Map<String, MCStockMeta>)serializer.deserializeObj( 
                                                               SER_FILE_NAME ) ;
        if( stockMetaMap == null ) {
            stockMetaMap = new HashMap<>() ;
        }
        
        collectStockDetailPageLinks() ;
        
        processMCStockMeta() ;
        
        serializeYAML( stockMetaMap ) ;
    }
    
    private void collectStockDetailPageLinks() {
        
        log.debug( "Scraping for NIFTY 500 stocks." ) ;
        
        browser.get( NIFTY_500_URL ) ;
        browser.waitForElement( By.id( "tableslider" ) ) ;
        
        if( browser.elementExists( By.id( "tableslider" ) ) ) {
            scrapeStockNames() ;
        }
    }
    
    private void scrapeStockNames() {
        
        String ROW_XPATH = "//*[@id=\"tableslider\"]//tbody/tr" ;
        List<WebElement> trs = browser.findElements( By.xpath( ROW_XPATH ) ) ;
        
        if( trs.size() > 1 ) {
            for( int i=0; i<trs.size(); i++ ) {
                String xPath = ROW_XPATH + "[" + (i+1) + "]/td[1]/a" ;
                WebElement a = browser.findElement( By.xpath( xPath ) ) ;
                
                String name = a.getText() ;
                String pageLink = a.getAttribute( "href" ) ;
                
                log.debug( "   -> " + a.getText() ) ;
                
                stockDetailPageLinks.put( name, pageLink ) ;
            }
        }
    }
    
    private void processMCStockMeta() throws Exception {
        
        List<String> exceptionList = new ArrayList<>() ;
        
        for( String key : stockDetailPageLinks.keySet() ) {
            
            if( !stockMetaMap.containsKey( key ) ) {
                
                String url = stockDetailPageLinks.get( key ) ;
                try {
                    extractAndSaveMeta( key, url ) ;
                    Thread.sleep( (int)(Math.random() * 1000) ) ;
                }
                catch( Exception e ) {
                    log.error( "Error extracting meta for " + key ) ;
                }
            }
            else {
                log.debug( "   Found " + key ) ;
                exceptionList.add( key ) ;
            }
        }
        
        if( exceptionList.isEmpty() ) {
            for( String exceptionKey : exceptionList ) {
                
                String url = stockDetailPageLinks.get( exceptionKey ) ;
                try {
                    extractAndSaveMeta( exceptionKey, url ) ;
                    Thread.sleep( (int)(Math.random() * 1000) ) ;
                }
                catch( Exception e ) {
                    log.error( "Error extracting meta for " + exceptionKey ) ;
                }
            }
        }
    }
    
    private void extractAndSaveMeta( String key, String url ) 
        throws Exception {
        
        String symbolXPath = "//*[@id=\"company_info\"]/ul/li[5]/ul/li[2]/p" ;
        String isinXPath   = "//*[@id=\"company_info\"]/ul/li[5]/ul/li[4]/p" ;
        String nameXPath   = "//*[@id=\"stockName\"]/h1" ;
        
        By symbolSelector = By.xpath( symbolXPath ) ;
        By isinSelector   = By.xpath( isinXPath ) ;
        By nameSelector   = By.xpath( nameXPath ) ;
        
        browser.get( url ) ;
        browser.waitForElement( isinSelector ) ;
        
        WebElement symbol = browser.findElement( symbolSelector ) ;
        WebElement isin   = browser.findElement( isinSelector ) ;
        WebElement name   = browser.findElement( nameSelector ) ;
        
        log.debug( "   " + key + " [ " + isin.getText() + " ]" ) ;
        
        MCStockMeta detail = new MCStockMeta() ;
        detail.setSymbolNSE  ( symbol.getText().trim() ) ;
        detail.setIsin       ( isin.getText().trim()   ) ;
        detail.setMcName     ( name.getText().trim()   ) ;
        detail.setDetailURL  ( url                     ) ;
        detail.setDescription( getDescription()        ) ;
        
        stockMetaMap.put( key, detail ) ;
        
        browser.postDataToServer( this.serverAddress, 
                                  "/Equity/Master/MCStockMeta", 
                                  detail ) ;

        serializer.serializeObj( stockMetaMap, SER_FILE_NAME ) ;
    }
    
    private void serializeYAML( Map<String, MCStockMeta> map ) {
        
        StringBuilder sb = new StringBuilder( "stockCfgs:\n" ) ;
        for( MCStockMeta meta : map.values() ) {
            sb.append( "   - isin : " + meta.getIsin() + "\n" ) ; 
            sb.append( "     mcName : " + meta.getMcName() + "\n" ) ; 
            sb.append( "     symbolNSE : " + meta.getSymbolNSE() + "\n" ) ; 
            sb.append( "     detailURL : " + meta.getDetailURL() + "\n" ) ;
            sb.append( "\n" ) ;
        }
        log.debug( sb.toString() ) ;
    }
    
    private String getDescription() throws Exception {
        
        String about = "" ;
        By aboutSelector = By.xpath( "//*[@class=\"morepls_cnt\"]" ) ;
        By moreSelector  = By.xpath( "//*[@class=\"vmore_plus\"]" ) ;
        
        if( browser.elementExists( aboutSelector ) ) {
            
            if( browser.elementExists( moreSelector ) ) {
                WebElement more = browser.findElement( moreSelector ) ;
                more.click() ;
            }
            
            WebElement div = browser.findElement( aboutSelector ) ;
            about = div.getText() ;
            about = about.replace( "view less -", "" ) ;
        }
        return about ;
    }
}

package com.sandy.automator.mc.stockisinmapper ;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;
import static org.apache.commons.lang.StringUtils.rightPad ;

import java.util.ArrayList ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class MapISINAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( MapISINAutomator.class ) ;
    
    private static final String PERSIST_FILE_KEY = "MCStockDetailMap" ;
    
    private static final String BASE_URL = 
            "https://www.moneycontrol.com/markets/indian-indices/" ;
    
    private static final String NIFTY_200_URL = BASE_URL + 
            "top-nse-200-companies-list/49?classic=true&categoryId=1&exType=N" ;
    
    private Browser browser = null ;
    private Serializer serializer = new Serializer() ;
    
    private String serverAddress = null ;
    
    private Map<String, String> stockDetailPageLinks = new LinkedHashMap<>() ;
    private Map<String, MCNameISIN> stockDetailMap = null ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        log.debug( "Executing MapISINAutomator" ) ;
        this.browser = browser ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        stockDetailMap = serializer.deserialize( PERSIST_FILE_KEY ) ;
        
        collectStockDetailPageLinks() ;
        collectNameISINMappings() ;
        postMappings() ;
    }
    
    private void collectStockDetailPageLinks() {
        
        log.debug( "Scraping for NIFTY 200 stocks." ) ;
        
        browser.get( NIFTY_200_URL ) ;
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
    
    private void collectNameISINMappings() 
        throws Exception {
        
        String isinXPath = "//*[@id=\"company_info\"]/ul/li[5]/ul/li[4]/p" ;
        
        for( String name : stockDetailPageLinks.keySet() ) {
            
            if( !stockDetailMap.containsKey( name ) ) {
                
                String url = stockDetailPageLinks.get( name ) ;
                By selector = By.xpath( isinXPath ) ;
                
                browser.get( url ) ;
                browser.waitForElement( selector ) ;
                
                WebElement isin = browser.findElement( selector ) ;
                
                log.debug( "   " + 
                           rightPad( name,  20 ) + 
                           " [" + isin.getText() + "]" ) ;
                
                MCNameISIN detail = new MCNameISIN() ;
                detail.setMcName( name ) ;
                detail.setIsin( isin.getText() ) ;
                detail.setDetailURL( url ) ;
                
                stockDetailMap.put( name, detail ) ;
                
                serializer.persist( stockDetailMap, PERSIST_FILE_KEY ) ;
            }
            else {
                log.debug( "   Found " + name ) ;
            }
        }
    }
    
    private void postMappings() 
        throws Exception {
        
        log.debug( "Posting data to server." ) ;
        List<MCNameISIN> mappings = new ArrayList<>() ;
        for( MCNameISIN value : stockDetailMap.values() ) {
            mappings.add( value ) ;
        }
        
        browser.postDataToServer( this.serverAddress, 
                                  "/Equity/Master/MCNameISIN", 
                                  mappings ) ;
    }
}

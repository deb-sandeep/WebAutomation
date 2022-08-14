package com.sandy.automator.mc.stockcharacteristics;

import static org.apache.commons.lang.StringUtils.leftPad ;
import static org.apache.commons.lang.StringUtils.rightPad ;

import java.util.Arrays ;
import java.util.List ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.mc.stockcharacteristics.StockAttributes.TechIndicator ;
import com.sandy.automator.mc.stockcharacteristics.cfg.StockConfig ;

public class StockAttributesExtractor {
    
    private static final Logger log = Logger.getLogger( StockAttributesExtractor.class ) ;

    private static final String BASE_MC_URL = "https://www.moneycontrol.com/india/stockpricequote/" ;
    
    private static String[][] OVERVIEW_META = {

        { "beta      ", "//*[@class=\"nsebeta\"]"             },
        { "high52    ", "//*[@class=\"nseH52 bseH52\"]"       },
        { "low52     ", "//*[@class=\"nseL52 bseL52\"]"       },
        { "eps       ", "//*[@class=\"nseceps bseceps\"]"     },
        { "pe        ", "//*[@class=\"nsepe bsepe\"]"         },
        { "sectorPE  ", "//*[@class=\"nsesc_ttm bsesc_ttm\"]" },
        { "pb        ", "//*[@class=\"nsepb bsepb\"]"         },
        { "isin      ", "//*[@id=\"company_info\"]/ul/li[5]/ul/li[4]/p" }
    } ;
    
    private Browser browser = null ;
    
    public StockAttributes extractAttributes( Browser browser,
                                              StockConfig cfg ) 
        throws Exception {
        
        this.browser = browser ;
        
        StockAttributes attributes = new StockAttributes() ;
        
        String url = BASE_MC_URL + cfg.getUrl() ;
        browser.get( url ) ;
        
        log.debug( "    Populating overview table values." ) ;
        populateOverviewTableData( attributes ) ;
        
        log.debug( "    Populating trend" ) ;
        String indLink = populateTrendData( attributes ) ;
        
        log.debug( "    Populating moving averages" ) ;
        populateMovingAverages( attributes ) ;
        
        log.debug( "    Populating community sentiments" ) ;
        populateCommunitySentiments( attributes ) ;
        
        // This has to be the last of population routines as this loads a 
        // separate URL.
        log.debug( "    Populating technical indicators" ) ;
        populateTechnicalIndicators( indLink, attributes ) ;
        
        attributes.setSymbolNSE( cfg.getSymbolNSE() ) ;
        return attributes ;
    }
    
    private void populateOverviewTableData( StockAttributes attribs ) 
        throws Exception {
        
        for( String[] meta : OVERVIEW_META ) {
            
            String property = meta[0] ;
            
            WebElement we = browser.findElement( By.xpath( meta[1] ) ) ;
            String value = we.getText() ;
            
            log.debug( "      " + property + " - " + value ) ;
            BeanUtils.setProperty( attribs, property.trim(), value ) ;
        }
    }
    
    private String populateTrendData( StockAttributes attribs ) 
        throws Exception {
        
        String TREND_ELEMENTS_XPATH = "//*[@id=\"dtechAnalysisDetails\"]/a[not(@style=\"display: none;\")]" ;
        By sel = By.xpath( TREND_ELEMENTS_XPATH ) ;
        
        browser.waitForElement( sel ) ;
        
        WebElement trendElement = browser.findElement( sel ) ;
        String trend = trendElement.getText() ;
        
        log.debug( "      " + trend ) ;
        attribs.setTrend( trend ) ;
        
        String detailsLink = trendElement.getAttribute( "href" ) ;
        return detailsLink ;
    }
    
    private void populateMovingAverages( StockAttributes attribs )
        throws Exception {
        
        String XPATH_TEMPLATE = "//*[@id=\"dmab\"]/tr[ROW_NUM]/td[2]" ;
        float[] mab = new float[6] ;
        
        for( int i=1; i<=6; i++ ) {
            
            String xPath = XPATH_TEMPLATE.replace( "ROW_NUM", "" + i ) ;
            By sel = By.xpath( xPath ) ;
            
            WebElement element = browser.findElement( sel ) ;
            mab[i-1] = Float.parseFloat( element.getText().replace( ",", "" ) ) ;
        }
        
        log.debug( "      " + Arrays.toString( mab ) ) ;
        attribs.setMovingAverages( mab ) ;
    }
    
    private void populateCommunitySentiments( StockAttributes attribs ) {
        
        String BASE_XPATH = "//*[@class=\"buy_sellper\"]" ;
        
        List<WebElement> elements = null ;
        int[] vals = new int[3] ;
        By containerSel  = By.xpath( BASE_XPATH ) ;
        
        if( browser.elementExists( containerSel ) ) {
            
            elements = browser.findElements( By.xpath( BASE_XPATH + "/li") ) ;
            for( int i=0; i<elements.size(); i++ ) {
                String val = elements.get( i ).getText() ;
                val = val.substring( 0, val.indexOf( '%' ) ) ;
                
                vals[i] = Integer.parseInt( val ) ;
            }
            log.debug( "      " + Arrays.toString( vals ) ) ;
        }
        else {
            log.debug( "      not found" ) ;
        }
        
        attribs.setCommunitySentiments( vals ) ;
    }

    private void populateTechnicalIndicators( String indLink, 
                                              StockAttributes attribs ) 
        throws Exception {
        
        String BASE_XPATH = "//*[@id=\"techindd\"]//tbody[2]/tr" ;
        
        browser.get( indLink ) ;
        browser.waitForElement( By.id( "techindd" ) ) ;
        
        List<WebElement> trs = null ;
        List<WebElement> tds = null ;
        
        trs = browser.findElements( By.xpath( BASE_XPATH ) ) ;
        
        for( int i=0; i<trs.size(); i++ ) {
            
            String colsXPath = BASE_XPATH + "[" + (i+1) + "]/td" ;
            tds = browser.findElements( By.xpath( colsXPath ) ) ;
            
            TechIndicator techInd = new TechIndicator() ;
            
            String name      = tds.get( 0 ).getText().trim() ;
            String level     = tds.get( 1 ).getText().trim() ;
            String indicator = tds.get( 2 ).getText().trim() ;
            
            if( name.contains( "Bollinger" ) ) {
                continue ;
            }
            else {
                techInd.setName( name ) ;
                techInd.setLevel( Float.parseFloat( level ) ) ;
                techInd.setIndication( indicator ) ;
                
                log.debug( "      " + 
                           rightPad( name, 20 ) + " - " + 
                           leftPad( level, 6 ) + " - " + 
                           rightPad( indicator, 15 ) ) ;
                
                attribs.getIndicators().add( techInd ) ;
            }
        }
    }
}

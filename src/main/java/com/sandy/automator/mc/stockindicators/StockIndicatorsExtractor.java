package com.sandy.automator.mc.stockindicators;

import static org.apache.commons.lang.StringUtils.leftPad ;
import static org.apache.commons.lang.StringUtils.rightPad ;

import java.text.SimpleDateFormat ;
import java.util.Arrays ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.mc.stockindicators.StockIndicators.TechIndicator ;
import com.sandy.automator.mc.stockindicators.cfg.StockConfig ;
import com.sandy.common.util.StringUtil ;

public class StockIndicatorsExtractor {
    
    private static final Logger log = Logger.getLogger( StockIndicatorsExtractor.class ) ;

    private static String[][] OVERVIEW_META = {

        { "beta           ", "//*[@class=\"nsebeta\"]"             },
        { "high52         ", "//*[@class=\"nseH52 bseH52\"]"       },
        { "low52          ", "//*[@class=\"nseL52 bseL52\"]"       },
        { "eps            ", "//*[@class=\"nseceps bseceps\"]"     },
        { "pe             ", "//*[@class=\"nsepe bsepe\"]"         },
        { "sectorPE       ", "//*[@class=\"nsesc_ttm bsesc_ttm\"]" },
        { "pb             ", "//*[@class=\"nsepb bsepb\"]"         },
        { "dividendYeild  ", "//*[@class=\"nsedy bsedy\"]"         },
        { "marketCap      ", "//*[@class=\"nsemktcap bsemktcap\"]" },
        { "sector         ", "//*[@id=\"stockName\"]/span/strong/a"},
        { "piotroskiScore ", "//*[starts-with(@class,\"nof\")]"              },
        { "isin           ", "//*[@id=\"company_info\"]/ul/li[5]/ul/li[4]/p" },
    } ;
    
    private static final SimpleDateFormat SDF = new SimpleDateFormat( "dd MMM, yyyy" ) ;
    
    private Browser browser = null ;
    
    public StockIndicators extractAttributes( Browser browser,
                                              StockConfig cfg ) 
        throws Exception {
        
        this.browser = browser ;
        
        StockIndicators attributes = new StockIndicators() ;
        
        attributes.setSymbolNse( cfg.getSymbolNSE() ) ;
        attributes.setIsin( cfg.getIsin() ) ;
        
        browser.get( cfg.getDetailURL() ) ;
        
        log.debug( "    Populating current price" ) ;
        populateCurrentPrice( attributes ) ;
        
        log.debug( "    Populating date" ) ;
        populateDate( attributes ) ;
        
        log.debug( "    Populating overview table values." ) ;
        populateOverviewTableData( attributes ) ;
        
        log.debug( "    Populating trend" ) ;
        String indLink = populateTrendData( attributes ) ;
        
        log.debug( "    Populating MC Essential Score" ) ;
        populateMCEssentialScore( attributes ) ;
        
        log.debug( "    Populating MC Insights" ) ;
        populateMCInsights( attributes ) ;
        
        log.debug( "    Populating CAGR values" ) ;
        populateCAGRValues( attributes ) ;
        
        log.debug( "    Populating moving averages" ) ;
        populateMovingAverages( attributes ) ;
        
        log.debug( "    Populating community sentiments" ) ;
        populateCommunitySentiments( attributes ) ;
        
        log.debug( "    Populating price performance" ) ;
        populatePricePerformance( attributes ) ;
        
        // This has to be the last of population routines as this loads a 
        // separate URL.
        log.debug( "    Populating technical indicators" ) ;
        populateTechnicalIndicators( indLink, attributes ) ;
        
        return attributes ;
    }
    
    private void populateCurrentPrice( StockIndicators attribs ) 
        throws Exception {
        
        By selector = By.id( "nsecp" ) ;
        
        browser.waitForElement( selector ) ;
        WebElement priceElement = browser.findElement( selector ) ;
        
        String price = priceElement.getAttribute( "rel" ) ;
        log.debug( "       " + price ) ;
        
        attribs.setCurrentPrice( Float.parseFloat( price ) ) ;
    }
    
    private void populateDate( StockIndicators attribs )
        throws Exception {
        
        By selector = By.xpath( "//*[@class=\"nseasondate\"]" ) ;
        browser.waitForElement( selector ) ;
        WebElement dateElement = browser.findElement( selector ) ;
        
        // Date is in the format 'As on 22 Aug, 2022 | 16:03'
        String dateStr = dateElement.getText().substring( 6, 18 ) ;
        
        Date asOnDate = SDF.parse( dateStr ) ;
        attribs.setAsOnDate( asOnDate ) ;
        
        log.debug( "       " + SDF.format( asOnDate ) ) ;
    }
    
    private void populateOverviewTableData( StockIndicators attribs ) 
        throws Exception {
        
        for( String[] meta : OVERVIEW_META ) {
            
            String property = meta[0] ;
            By     selector = By.xpath( meta[1] ) ;
            
            if( browser.elementExists( selector ) ) {

                WebElement we = browser.findElement( By.xpath( meta[1] ) ) ;
                String value = we.getText() ;
                value = value.replace( ",", "" ) ;
                
                log.debug( "       " + property + " - " + value ) ;
                BeanUtils.setProperty( attribs, property.trim(), value ) ;
            }
        }
    }
    
    private String populateTrendData( StockIndicators attribs ) 
        throws Exception {
        
        String TREND_ELEMENTS_XPATH = "//*[@id=\"dtechAnalysisDetails\"]/a[not(@style=\"display: none;\")]" ;
        By sel = By.xpath( TREND_ELEMENTS_XPATH ) ;
        
        browser.waitForElement( sel ) ;
        
        WebElement trendElement = browser.findElement( sel ) ;
        String trend = trendElement.getText() ;
        
        log.debug( "       " + trend ) ;
        attribs.setTrend( trend ) ;
        
        String detailsLink = trendElement.getAttribute( "href" ) ;
        return detailsLink ;
    }
    
    private void populateMCEssentialScore( StockIndicators attribs )
        throws Exception {
        
        String MCESSENTIALS_ELEMENTS_XPATH = "//*[starts-with(@class,\"esbx\")]" ;
        By sel = By.xpath( MCESSENTIALS_ELEMENTS_XPATH ) ;
        
        browser.waitForElement( sel ) ;
        
        WebElement trendElement = browser.findElement( sel ) ;
        String mceText = trendElement.getText() ;
        int score = Integer.parseInt( mceText.substring( 0, mceText.indexOf( '%' ) ) ) ;
        
        log.debug( "       " + score ) ;
        attribs.setMcEssentialScore( score ) ;
    }
    
    private void populateMCInsights( StockIndicators attributes ) {
        
        WebElement e = null ;
        
        String insightShortXPath = "(//*[@class=\"mcinperf\"])[2]/div" ;
        String insightLongXPath  = "//*[@class=\"insightRight\"]" ;
        
        By shortSel = By.xpath( insightShortXPath ) ;
        By longSel  = By.xpath( insightLongXPath ) ;
        
        browser.waitForElement( longSel ) ;
        
        if( browser.elementExists( shortSel ) ) {
            e = browser.findElement( shortSel ) ;
            attributes.setMcInsightShort( e.getText().trim() ) ;
            log.debug( "       " + attributes.getMcInsightShort() ) ;
        }
        else {
            attributes.setMcInsightShort( "N/A" ) ;
        }
        
        if( browser.elementExists( longSel ) ) {
            e = browser.findElement( longSel ) ;
            attributes.setMcInsightLong( e.getText().trim() ) ;
            log.debug( "       " + attributes.getMcInsightLong() ) ;
        }
        else {
            attributes.setMcInsightShort( "N/A" ) ;
        }
    }

    private void populateCAGRValues( StockIndicators attributes ) {
        
        String cagrTableXPath   = "//*[@class=\"frevdat\"]" ;
        String cagrTableTDXPath = "//*[@class=\"frevdat\"]//td" ;
        By     tableSelector    = By.xpath( cagrTableXPath ) ;
        
        if( browser.elementExists( tableSelector ) ) {
            
            By tdSelector = By.xpath( cagrTableTDXPath ) ;
            List<WebElement> tds = browser.findElements( tdSelector ) ;
            
            attributes.setCagrRevenue  ( removePct( tds.get( 1 ) ) ) ;
            attributes.setCagrNetProfit( removePct( tds.get( 3 ) ) ) ;
            attributes.setCagrEbit     ( removePct( tds.get( 5 ) ) ) ;
            
            log.debug( "       CAGR Revenue    = " + attributes.getCagrRevenue() ) ;
            log.debug( "       CAGR Net Profit = " + attributes.getCagrNetProfit() ) ;
            log.debug( "       CAGR Ebit       = " + attributes.getCagrEbit() ) ;
        }
    }
    
    private float removePct( WebElement td ) {
        String val = td.getText() ;
        val = val.replace( "%", "" ) ;
        if( StringUtil.isNotEmptyOrNull( val ) ) {
            return Float.parseFloat( val.trim() ) ;
        }
        return 0 ;
    }

    private void populateMovingAverages( StockIndicators attribs )
        throws Exception {
        
        String XPATH_TEMPLATE = "//*[@id=\"dmab\"]/tr[ROW_NUM]/td[2]" ;
        float[] mab = new float[6] ;
        
        for( int i=1; i<=6; i++ ) {
            
            String xPath = XPATH_TEMPLATE.replace( "ROW_NUM", "" + i ) ;
            By sel = By.xpath( xPath ) ;
            
            WebElement element = browser.findElement( sel ) ;
            mab[i-1] = Float.parseFloat( element.getText().replace( ",", "" ) ) ;
        }
        
        log.debug( "       " + Arrays.toString( mab ) ) ;
        
        attribs.setSma5  ( mab[0] ) ;
        attribs.setSma10 ( mab[1] ) ;
        attribs.setSma20 ( mab[2] ) ;
        attribs.setSma50 ( mab[3] ) ;
        attribs.setSma100( mab[4] ) ;
        attribs.setSma200( mab[5] ) ;
    }
    
    private void populateCommunitySentiments( StockIndicators attribs ) {
        
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
            log.debug( "       " + Arrays.toString( vals ) ) ;
        }
        else {
            log.debug( "       not found" ) ;
        }
        
        attribs.setCommunitySentimentBuy ( vals[0] ) ;
        attribs.setCommunitySentimentSell( vals[1] ) ;
        attribs.setCommunitySentimentHold( vals[2] ) ;
    }

    private void populatePricePerformance( StockIndicators attributes ) 
        throws Exception {
        
        String[][] xPathMap = {
                { "pricePerf1W  ", "//*[@class=\"nsepc1w bsepc1w\"]/td"   },
                { "pricePerf1M  ", "//*[@class=\"nsepc1m bsepc1m\"]/td"   },
                { "pricePerf3M  ", "//*[@class=\"nsepc3m bsepc3m\"]/td"   },
                { "pricePerfYTD ", "//*[@class=\"nsepcYtd bsepcYtd\"]/td" },
                { "pricePerf1Y  ", "//*[@class=\"nsepc1y bsepc1y\"]/td"   },
                { "pricePerf3Y  ", "//*[@class=\"nsepc3y bsepc3y\"]/td"   },
        } ;
        
        for( String[] pair : xPathMap ) {
            
            List<WebElement> tds = browser.findElements( By.xpath( pair[1] ) ) ;
            
            for( int i=1; i<3; i++ ) {
                
                String value = tds.get( i ).getText().trim() ;
                if( value.contains( "%" ) ) {
                    
                    value = value.substring( 0, value.indexOf( '%' ) ) ;
                    
                    log.debug( "       " + pair[0] + " - " + value ) ;
                    BeanUtils.setProperty( attributes, pair[0].trim(), value ) ;
                    break ;
                }
            }
            
        }
    }

    private void populateTechnicalIndicators( String indLink, 
                                              StockIndicators attribs ) 
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
                level = level.replace( ",", "" ) ;
                
                techInd.setName( name ) ;
                techInd.setLevel( Float.parseFloat( level ) ) ;
                techInd.setIndication( indicator ) ;
                
                log.debug( "       " + 
                           rightPad( name, 20 ) + " - " + 
                           leftPad( level, 6 ) + " - " + 
                           rightPad( indicator, 15 ) ) ;
                
                attribs.getIndicators().add( techInd ) ;
            }
        }
    }
    
    public static void main( String[] args ) throws Exception {
        
        String dateStr = "As on 22 Aug, 2022 | 16:03" ;
        SimpleDateFormat df = new SimpleDateFormat( "dd MMM, yyyy" ) ;
        
        String str = dateStr.substring( 6, 18 ) ;
        log.debug( str );
        
        log.debug( df.parse( str ) ) ;
        log.debug( df.format( new Date() ) );
    }
}

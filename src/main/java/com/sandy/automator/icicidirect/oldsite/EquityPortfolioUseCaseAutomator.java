package com.sandy.automator.icicidirect.oldsite;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.File ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.support.ui.Select ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.icicidirect.vo.equity.EquityHolding ;
import com.sandy.automator.icicidirect.vo.equity.EquityTxnPosting ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

public class EquityPortfolioUseCaseAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( EquityPortfolioUseCaseAutomator.class ) ;
    
    private static final By HOLDING_TYPE_SELECT = By.id( "Holdings" ) ;
    private static final By VIEW_BTN = By.id( "subm2" ) ;
    private static final By ADV_OPTIONS = By.id( "hypfilter" ) ;
    private static final By HOLDING_CSV_DN_SELECTOR = By.cssSelector(  "#dvfilter > div:nth-child(2) > ul > li:nth-child(2) > img:nth-child(2)" ) ;
    private static final By TXNS_CSV_DN_SELECTOR = By.cssSelector(  "#dvfilter > div:nth-child(2) > ul > li:nth-child(4) > img:nth-child(2)" ) ;
    
    private static final SimpleDateFormat SDF = new SimpleDateFormat( "dd-MMM-yyyy" ) ;
    
    private Browser browser = null ;
    private SiteCredential cred = null ;
    private CsvParser csvParser = null ;
    private ICICIDirectOldSiteAutomator siteAutomator = null ;
    private String serverAddress = null ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.cred = cred ;
        this.siteAutomator = ( ICICIDirectOldSiteAutomator )getSiteAutomator() ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        processPortfolio() ;
    }
    
    private void processPortfolio() throws Exception {
        
        log.debug( "Navigating to equity portfolio & statements section" ) ;
        
        siteAutomator.gotoSection( OldSiteSection.TI ) ;
        siteAutomator.gotoSection( OldSiteSection.TI_PS ) ;
        siteAutomator.gotoSection( OldSiteSection.TI_PS_EQ ) ;
        
        showAllHoldings() ;
        processHoldingSummary() ;
        processHoldingTxns() ;
        
        Thread.sleep( 5000 ) ;
    }
    
    private void showAllHoldings() throws Exception {
        
        log.debug( "Showing all holdings." ) ;
        
        WebElement element = null ;
        
        browser.waitForElement( HOLDING_TYPE_SELECT ) ;
        element = browser.findElement( HOLDING_TYPE_SELECT ) ;
        Select dropdown = new Select( element ) ;
        dropdown.selectByVisibleText( "All" ) ;
        
        element = browser.findElement( VIEW_BTN ) ;
        element.click() ;
        Thread.sleep( 3000 ) ;
        
        element = browser.findElement( ADV_OPTIONS ) ;
        element.click() ;
        Thread.sleep( 1000 ) ;
    }
    
    private void processHoldingSummary() throws Exception {
        
        log.debug( "Processing holding summary." ) ;
        File csvFile = null ;
        List<EquityHolding> holdings = null ;
        
        csvFile = browser.downloadFile( HOLDING_CSV_DN_SELECTOR ) ;
        holdings = parseEquityHoldings( cred.getIndividualName(), csvFile ) ;
        browser.postDataToServer( this.serverAddress, 
                                  "/Equity/Holding", 
                                  holdings ) ;
    }
    
    private void processHoldingTxns() throws Exception {
        
        log.debug( "Processing holding transactions." ) ;
        File csvFile = null ;
        List<EquityTxnPosting> txns = null ;
        
        csvFile = browser.downloadFile( TXNS_CSV_DN_SELECTOR ) ;
        txns = parseEquityTxns( cred.getIndividualName(), csvFile ) ;
        browser.postDataToServer( this.serverAddress, 
                                  "/Equity/Transaction", 
                                  txns ) ;
    }

    private List<EquityHolding> parseEquityHoldings( String ownerName, File csvFile ) {
        
        this.csvParser = getHoldingSummaryCsvParser() ;
        
        List<EquityHolding> assets = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;
        
        for( int i=1; i<csvFileContents.size(); i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            EquityHolding holding = buildEquityHolding( ownerName, tupule ) ;
            assets.add( holding ) ;
            log.debug( holding ) ;
        }
        return assets ;
    }
    
    private CsvParser getHoldingSummaryCsvParser() {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( 
            "Stock Symbol",
            "Company Name",
            "ISIN Code",
            "Qty",
            "Average Cost Price",
            "Current Market Price",
            "Realized Profit / Loss"
        ) ;
        
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }
    
    private EquityHolding buildEquityHolding( String ownerName, String[] tupule ) {
        EquityHolding holding = new EquityHolding() ;
        
        holding.setOwnerName( ownerName ) ;
        holding.setSymbolIcici( tupule[0].trim() ) ;
        holding.setCompanyName( tupule[1].trim() ) ;
        holding.setIsin( tupule[2].trim() ) ;
        holding.setQuantity( Integer.parseInt( tupule[3].trim() ) ) ;
        holding.setAvgCostPrice( parseFloatAmt( tupule[4].trim() ) ) ;
        holding.setCurrentMktPrice( parseFloatAmt( tupule[5].trim() ) ) ;
        holding.setRealizedProfitLoss( parseFloatAmt( tupule[6].trim() ) ) ;
        holding.setLastUpdate( new Date() ) ;

        return holding ;
    }
    
    private List<EquityTxnPosting> parseEquityTxns( String ownerName,
                                                    File csvFile ) 
        throws Exception {
        
        this.csvParser = getEquityTxnsCsvParser() ;
        
        List<EquityTxnPosting> txns = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;
        
        for( int i=1; i<csvFileContents.size(); i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            EquityTxnPosting txn = buildEquityTxn( ownerName, tupule ) ;
            txns.add( txn ) ;
            log.debug( txn ) ;
        }
        return txns ;
    }

    private CsvParser getEquityTxnsCsvParser() {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( 
                "Stock Symbol",
                "Action",
                "Quantity",
                "Transaction Price",
                "Brokerage",
                "Transaction Charges",
                "StampDuty",
                "Transaction Date"
        ) ;
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }

    private EquityTxnPosting buildEquityTxn( String ownerName, String[] tupule ) 
        throws Exception {
        
        EquityTxnPosting txn = new EquityTxnPosting() ;
        
        txn.setOwnerName( ownerName ) ;
        txn.setSymbolICICI( tupule[0].trim() ) ;
        txn.setAction( tupule[1].trim() ) ;
        txn.setQuantity( Integer.parseInt( tupule[2].trim() ) ) ;
        txn.setTxnPrice( parseFloatAmt( tupule[3].trim() ) ) ;
        txn.setBrokerage( parseFloatAmt( tupule[4].trim() ) ) ;
        txn.setTxnCharges( parseFloatAmt( tupule[5].trim() ) ) ;
        txn.setStampDuty( parseFloatAmt( tupule[6].trim() ) ) ;
        txn.setTxnDate( SDF.parse( tupule[7].trim() ) ) ;

        return txn ;
    }

    private float parseFloatAmt( String input ) {
        if( input.equals( "NA" ) ) {
            return 0 ;
        }
        
        if( input.startsWith( "(" ) ) {
            input = input.substring( 1, input.length()-1 ) ;
            return -1*Float.parseFloat( input ) ;
        }
        return Float.parseFloat( input ) ;
    }
}

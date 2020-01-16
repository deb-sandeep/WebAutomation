package com.sandy.automator.icicidirect.mf;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.support.ui.Select ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.icicidirect.ICICIDirectSiteAutomator ;
import com.sandy.automator.icicidirect.SiteSection ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

public class MFPortfolioUseCaseAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( MFPortfolioUseCaseAutomator.class ) ;
    
    private static final By CSV_DN_SELECTOR = By.cssSelector(  "img[title='Summary:Export To CSV']" ) ;
    private static final By HOLDING_TYPE_SELECT = By.id( "DDL_Status" ) ;
    private static final By VIEW_BTN = By.id( "Viewbut" ) ;
    private static final By MF_TXN_BACK_BTN = By.linkText( "Back" ) ;
    
    private static final String XPATH_TXN_DETAIL_TBODY = "//*[@id=\"divPortfolioDetailsData\"]/table/tbody" ;
    
    private Browser browser = null ;
    private ICICIDirectSiteAutomator siteAutomator = null ;
    private SiteCredential cred = null ;
    private CsvParser csvParser = null ;
    private String serverAddress = null ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.cred = cred ;
        this.csvParser = getCsvParser() ;
        this.siteAutomator = ( ICICIDirectSiteAutomator )getSiteAutomator() ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        processPortfolio() ;
    }
    
    private void processPortfolio() throws Exception {
        
        List<MutualFundAsset> mfAssets = null ;
        File csvFile = null ;
        
        siteAutomator.gotoSection( SiteSection.TI ) ;
        siteAutomator.gotoSection( SiteSection.TI_PS ) ;
        siteAutomator.gotoSection( SiteSection.TI_PS_MF ) ;
        
        showAllHoldings() ;
        
        csvFile = browser.downloadFile( CSV_DN_SELECTOR ) ;
        mfAssets = parseMFAssets( cred.getIndividualName(), csvFile ) ;
        
        browser.postDataToServer( this.serverAddress, 
                                  "/MutualFund/Portfolio", 
                                  mfAssets ) ;
        
        List<MFTxn> txnList = new ArrayList<>() ;
        for( MutualFundAsset mfAsset : mfAssets ) {
            txnList.clear() ;
            processMFTransactionHistory( mfAsset, txnList ) ;
            showAllHoldings() ;
            browser.postDataToServer( this.serverAddress, 
                                      "/MutualFund/TxnList", 
                                      txnList ) ;
        }
    }
    
    private List<MutualFundAsset> parseMFAssets( String ownerName, File csvFile ) {
        
        List<MutualFundAsset> assets = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;
        
        for( int i=1; i<csvFileContents.size(); i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            MutualFundAsset mfAsset = buildMFAsset( ownerName, tupule ) ;
            log.debug( mfAsset.getScheme() ) ;
            assets.add( mfAsset ) ;
        }
        
        return assets ;
    }
    
    private void processMFTransactionHistory( MutualFundAsset mf,
                                              List<MFTxn> txnList ) 
        throws Exception {
        
        // Go to the MF detail page and wait till the page loads
        String linkText = mf.getScheme().replace( "  ", " " ) ;
        log.debug( "Processing transaction history for MF asset - " + linkText ) ;
        
        browser.clickElement( By.linkText( linkText ) ) ;
        browser.waitForElement( MF_TXN_BACK_BTN ) ;
        
        processMFTxnHistoryTable( mf.getOwnerName(), txnList ) ;
        
        // Go back to the MF summary page and wait till the page loads
        browser.clickElement( MF_TXN_BACK_BTN ) ;
        browser.waitForElement( VIEW_BTN ) ;
    }
    
    private void processMFTxnHistoryTable( String ownerName,
                                           List<MFTxn> txnList ) 
        throws Exception {
        
        List<WebElement> rows = browser.findElements( By.xpath( XPATH_TXN_DETAIL_TBODY + "/tr" ) ) ;
        log.debug( rows.size() + " transactions found." ) ;
        for( int rowNum=1; rowNum<=rows.size(); rowNum++ ) {
            String rowXPath = XPATH_TXN_DETAIL_TBODY + "/tr[" + rowNum + "]" ;
            MFTxn txn = new MFTxn() ;
            txn.setOwnerName( ownerName ) ;
            txn.setScheme( getTxnAttr( rowXPath, 2 ) );
            txn.setTxnDate( MFTxn.SDF.parse( getTxnAttr( rowXPath, 5 ) ) ) ;
            txn.setTxnType( getTxnAttr( rowXPath, 6 ) ) ;
            txn.setNavPerUnit( Float.parseFloat( getTxnAttr( rowXPath, 8 ) ) ) ;
            txn.setNumUnits( Float.parseFloat( getTxnAttr( rowXPath, 9 ) ) ) ;
            txn.setAmount( Float.parseFloat( getTxnAttr( rowXPath, 10 ) ) ) ;
            txn.setTxnChannel( getTxnAttr(  rowXPath, 11 ) ) ;
            
            txnList.add( txn ) ;
            log.debug( "\t" + txn.getShortString() ) ;
        }
    }
    
    private String getTxnAttr( String rowXPath, int colNum ) {
        String cellXPath = rowXPath + "/td[" + colNum + "]" ;
        WebElement element = browser.findElement( By.xpath( cellXPath ) ) ;
        return element.getText() ;
    }
    
    private CsvParser getCsvParser() {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( 
            "Scheme",
            "Category",
            "Sub Category",
            "Units Held",
            "Average Cost Price",
            "Value At Cost",
            "Last Recorded NAV *",
            "Value at NAV",
            "Profit/ Loss",
            "Profit/ Loss %"
        ) ;
        
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }
    
    private MutualFundAsset buildMFAsset( String ownerName, String[] tupule ) {
        MutualFundAsset asset = new MutualFundAsset() ;
        
        asset.setOwnerName( ownerName ) ;
        asset.setScheme( tupule[0].replace( "  ", " " ) ) ;
        asset.setCategory( tupule[1] ) ;
        asset.setSubCategory( tupule[2] ) ;
        asset.setUnitsHeld( Float.parseFloat( tupule[3] ) ) ;
        asset.setAvgCostPrice( Float.parseFloat( tupule[4] ) ) ;
        asset.setValueAtCost( Float.parseFloat( tupule[5] ) ) ;
        asset.setLastRecordedNav( Float.parseFloat( tupule[6] ) ) ;
        asset.setValueAtNav(  Float.parseFloat( tupule[7] ) ) ;
        asset.setProfitLossAmt( Float.parseFloat( tupule[8] ) ) ;
        asset.setProfitLossPct( Float.parseFloat( tupule[9] ) ) ;

        return asset ;
    }
    
    private void showAllHoldings() throws Exception {
        
        WebElement element = null ;
        
        browser.waitForElement( HOLDING_TYPE_SELECT ) ;
        element = browser.findElement( HOLDING_TYPE_SELECT ) ;
        Select dropdown = new Select( element ) ;
        dropdown.selectByVisibleText( "All" ) ;
        
        element = browser.findElement( VIEW_BTN ) ;
        element.click() ;
        Thread.sleep( 1000 ) ;
    }
}

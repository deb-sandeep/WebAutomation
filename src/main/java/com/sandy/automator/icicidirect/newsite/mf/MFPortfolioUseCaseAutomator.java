package com.sandy.automator.icicidirect.newsite.mf;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.File ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.icicidirect.newsite.ICICIDirectNewSiteAutomator ;
import com.sandy.automator.icicidirect.newsite.NewSiteSection ;
import com.sandy.automator.icicidirect.vo.mf.MFTxn ;
import com.sandy.automator.icicidirect.vo.mf.MutualFundAsset ;

public class MFPortfolioUseCaseAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( MFPortfolioUseCaseAutomator.class ) ;
    
    private static final String XPATH_TXN_DETAIL_TBODY = 
    //"//*[@id=\"pnlPfView\" and @ea_visible=\"true\"]/div/div/div[2]/div/div/table/tbody" ;
    "(//*/div[@id=\"pnlPfView\"])[last()]/div/div/div[2]/div/div/table/tbody" ;
    
    private Browser browser = null ;
    private ICICIDirectNewSiteAutomator siteAutomator = null ;
    private SiteCredential cred = null ;
    private String serverAddress = null ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.cred = cred ;
        this.siteAutomator = ( ICICIDirectNewSiteAutomator )getSiteAutomator() ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        processPortfolio() ;
    }
    
    private void processPortfolio() throws Exception {
        
        List<MutualFundAsset> mfAssets = null ;
        
        log.debug( "\n>> Navigating to MF portfolio section" ) ;
        
        siteAutomator.gotoSection( NewSiteSection.SECTION_PORTFOLIO ) ;
        siteAutomator.gotoSection( NewSiteSection.SUBSECTION_MF ) ;
        
        log.debug( "  # Sleeping for 5 seconds to clear any dialogs" ) ;
        Thread.sleep( 5000 ) ;
        
        showAllHoldings() ;
        Thread.sleep( 3000 ) ;
        
        mfAssets = processHoldingSummary() ;
        Thread.sleep( 3000 ) ;
        
        processMFTransactionHistory( mfAssets ) ;
        Thread.sleep( 3000 ) ;
    }
    
    private void showAllHoldings() throws Exception {
        
        log.debug( "\n>> Showing all holdings." ) ;
        
        WebElement element = null ;
        
        // Find and click the drop down icon which will open the holding selection
        log.debug( "  # Clicking the holding dropdown expand icon" ) ;
        By holdingTypeSelect = By.xpath( "//*[@id=\"DDL_Status-button\"]/span[1]" ) ;
        browser.waitForElement( holdingTypeSelect );
        element = browser.findElement( holdingTypeSelect ) ;
        element.click() ;
        
        // Select the 'All' option in the holding type dropdown
        log.debug( "  # Clicking the All option" ) ;
        By allOption = By.id( "ui-id-3" ) ;
        browser.waitForElement( allOption );
        element = browser.findElement( allOption ) ;
        element.click() ;
        
        // Apply the options by clicking the view button.
        log.debug( "  # Clicking the view button" ) ;
        By viewBtn = By.xpath( "//*[@id=\"MFPortfolioDiv\"]/div[2]/div[1]/ul/li[4]/label/input" ) ;
        browser.waitForElement( viewBtn );
        element = browser.findElement( viewBtn ) ;
        element.click() ;
    }

    private  List<MutualFundAsset> processHoldingSummary() throws Exception {
        
        log.debug( "\n>> Processing holding summary" ) ;

        By selector = null ;
        File csvFile = null ;
        WebElement element = null ;
        List<MutualFundAsset> holdings = null ;
        MFHoldingsCSVParser csvParser = new MFHoldingsCSVParser() ;
        
        log.debug( "  # Clicking the Download link" ) ;
        element = browser.findByLinkText( "Download" ) ;
        element.click() ;
        
        log.debug( "  # Downloading Summary: CSV file" ) ;
        selector = By.linkText( "CSV" ) ;
        browser.waitForElement( selector );
        csvFile = browser.downloadFile( selector ) ;
        
        log.debug( "  # Parsing the CSV file." ) ;
        holdings = csvParser.parseMFHoldings( cred.getIndividualName(), csvFile ) ;
        
        log.debug( "  # Posting the holding summaries to server." ) ;
        log.debug( "    Num holdings - " + holdings.size() ) ;
        
        browser.postDataToServer( this.serverAddress, 
                                  "/MutualFund/Portfolio", 
                                  holdings ) ;
        return holdings ;
    }
    
    private void processMFTransactionHistory( List<MutualFundAsset> mfAssets ) 
        throws Exception {
        
        List<MFTxn> txnList = new ArrayList<>() ;
        for( MutualFundAsset mfAsset : mfAssets ) {
            txnList.clear() ;
            
            log.debug( "\n>> Processing transaction history - " + mfAsset.getScheme() ) ;

            processMFTransactionHistory( mfAsset, txnList ) ;

            log.debug( "    Num transactions - " + txnList.size() ) ;
            if( !txnList.isEmpty() ) {
                log.debug( "  # Posting txn list to server." ) ;
                browser.postDataToServer( this.serverAddress, 
                                          "/MutualFund/TxnList", 
                                          txnList ) ;
            }
        }
    }
    
    private void processMFTransactionHistory( MutualFundAsset mf,
                                              List<MFTxn> txnList ) 
        throws Exception {
        
        // Go to the MF detail page and wait till the page loads
        String linkText = mf.getScheme().toUpperCase() ;
        
        log.debug( "  # Opening txn history table" ) ;
        browser.clickElement( By.linkText( linkText ) ) ;
        Thread.sleep( 3000 ) ;
        
        processMFTxnHistoryTable( mf, txnList ) ;
        
        // Collapse the transaction section of the current fund
        browser.clickElement( By.linkText( linkText ) ) ;
    }
    
    private void processMFTxnHistoryTable( MutualFundAsset mf,
                                           List<MFTxn> txnList ) 
        throws Exception {
        
        By bodyRowSelector = By.xpath( XPATH_TXN_DETAIL_TBODY + "/tr" ) ;
        List<WebElement> rows = browser.findElements( bodyRowSelector ) ;
        
        log.debug( "    " + rows.size() + " transactions found" ) ;
        
        for( int rowNum=1; rowNum<=rows.size(); rowNum++ ) {
            String rowXPath = XPATH_TXN_DETAIL_TBODY + "/tr[" + rowNum + "]" ;
            MFTxn txn = new MFTxn() ;
            
            txn.setOwnerName( mf.getOwnerName() ) ;
            txn.setScheme   ( mf.getScheme() ) ;
            
            txn.setTxnDate   ( getTxnAttrAsDate  ( rowXPath, 1 ) ) ;
            txn.setTxnType   ( getTxnAttrAsString( rowXPath, 2 ) ) ;
            txn.setNavPerUnit( getTxnAttrAsFloat ( rowXPath, 4 ) ) ;
            txn.setNumUnits  ( getTxnAttrAsFloat ( rowXPath, 5 ) ) ;
            txn.setAmount    ( getTxnAttrAsFloat ( rowXPath, 6 ) ) ;
            txn.setTxnChannel( getTxnAttrAsString( rowXPath, 7 ) ) ;
            
            txnList.add( txn ) ;
            log.debug( "      " + txn.getShortString() ) ;
        }
        log.debug( "\n" ) ;
    }
    
    private String getTxnAttrAsString( String rowXPath, int colNum ) {
        String cellXPath = rowXPath + "/td[" + colNum + "]" ;
        WebElement element = browser.findElement( By.xpath( cellXPath ) ) ;
        return element.getText() ;
    }

    private Date getTxnAttrAsDate( String rowXPath, int colNum ) 
        throws Exception {
        String txt = getTxnAttrAsString( rowXPath, colNum ) ;
        return MFTxn.SDF.parse( txt ) ;
    }

    private float getTxnAttrAsFloat( String rowXPath, int colNum ) 
        throws Exception {
        String txt = getTxnAttrAsString( rowXPath, colNum ) ;
        txt = txt.replace( ",", "" ) ;
        return Float.parseFloat( txt ) ;
    }
}

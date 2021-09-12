package com.sandy.automator.icicidirect.newsite.equity;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.File ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.icicidirect.newsite.ICICIDirectNewSiteAutomator ;
import com.sandy.automator.icicidirect.newsite.NewSiteSection ;
import com.sandy.automator.icicidirect.vo.equity.EquityHolding ;
import com.sandy.automator.icicidirect.vo.equity.EquityTxnPosting ;

public class EquityPortfolioUseCaseAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( EquityPortfolioUseCaseAutomator.class ) ;
    
    private Browser browser = null ;
    private SiteCredential cred = null ;
    private ICICIDirectNewSiteAutomator siteAutomator = null ;
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
        
        log.debug( "\n>> Navigating to equity portfolio section" ) ;
        
        siteAutomator.gotoSection( NewSiteSection.SECTION_PORTFOLIO ) ;
        siteAutomator.gotoSection( NewSiteSection.SUBSECTION_EQUITY ) ;
        
        log.debug( "  # Sleeping for 5 seconds to clear any dialogs" ) ;
        Thread.sleep( 5000 ) ;
        
        showAllHoldings() ;
        Thread.sleep( 3000 ) ;

        processHoldingSummary() ;
        Thread.sleep( 3000 ) ;

        processHoldingTxns() ;
        Thread.sleep( 5000 ) ;
    }
    
    private void showAllHoldings() throws Exception {
        
        log.debug( "\n>> Showing all holdings." ) ;
        
        WebElement element = null ;
        
        // Find and click the drop down icon which will open the holding selection
        log.debug( "  # Clicking the holding dropdown expand icon" ) ;
        By holdingTypeSelect = By.xpath( "//*[@id=\"Holdings-button\"]/span[1]" ) ;
        browser.waitForElement( holdingTypeSelect );
        element = browser.findElement( holdingTypeSelect ) ;
        element.click() ;
        
        // Select the 'All' option in the holding type dropdown
        log.debug( "  # Clicking the All option" ) ;
        By allOption = By.id( "ui-id-5" ) ;
        browser.waitForElement( allOption );
        element = browser.findElement( allOption ) ;
        element.click() ;
        
        // Apply the options by clicking the view button.
        log.debug( "  # Clicking the view button" ) ;
        By viewBtn = By.id( "subm2" ) ;
        browser.waitForElement( viewBtn );
        element = browser.findElement( viewBtn ) ;
        element.click() ;
    }
    
    private void processHoldingSummary() throws Exception {
        
        log.debug( "\n>> Processing holding summary" ) ;

        By selector = null ;
        File csvFile = null ;
        WebElement element = null ;
        List<EquityHolding> holdings = null ;
        EquityHoldingsCSVParser csvParser = new EquityHoldingsCSVParser() ;
        
        log.debug( "  # Clicking the Download link" ) ;
        element = browser.findByLinkText( "Download" ) ;
        element.click() ;
        
        log.debug( "  # Downloading Summary: CSV file" ) ;
        selector = By.linkText( "Summary: CSV" ) ;
        browser.waitForElement( selector );
        csvFile = browser.downloadFile( selector ) ;
        
        log.debug( "  # Parsing the CSV file." ) ;
        holdings = csvParser.parseEquityHoldings( cred.getIndividualName(), csvFile ) ;
        
        log.debug( "  # Posting the holding summaries to server." ) ;
        log.debug( "    Num holdings - " + holdings.size() ) ;
        browser.postDataToServer( this.serverAddress, 
                                  "/Equity/Holding", 
                                  holdings ) ;
    }
    
    private void processHoldingTxns() throws Exception {
        
        log.debug( "\n>> Processing holding transactions" ) ;

        File csvFile = null ;
        List<EquityTxnPosting> txns = null ;
        WebElement element = null ;
        By selector = null ;
        EquityTxnsCSVParser csvParser = new EquityTxnsCSVParser() ;
        
        log.debug( "  # Clicking the Download link" ) ;
        element = browser.findByLinkText( "Download" ) ;
        element.click() ;
        
        log.debug( "  # Downloading All Transaction: CSV file" ) ;
        selector = By.linkText( "All Transaction: CSV" ) ;
        browser.waitForElement( selector );
        csvFile = browser.downloadFile( selector ) ;

        log.debug( "  # Parsing the CSV file." ) ;
        txns = csvParser.parseEquityTxns( cred.getIndividualName(), csvFile ) ;

        log.debug( "  # Posting the holding summaries to server." ) ;
        log.debug( "    Num transactions - " + txns.size() ) ;
        browser.postDataToServer( this.serverAddress, 
                                  "/Equity/Transaction", 
                                  txns ) ;
    }
}

package com.sandy.automator.icicibank.cc;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class CCEntryParseUCAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( CCEntryParseUCAutomator.class ) ;
    
    private Browser browser = null ;
    private String serverAddress = null ;
    private float outstandingDues = 0 ;
    private String creditCardNumber = null ;
    
    @Override
    public boolean canExecuteForCredential( SiteCredential cred ) {
        return cred.getBooleanAttribute( "hasCC" ) ;
    }
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.creditCardNumber = cred.getAttribute( "creditCardNumber" ) ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        Thread.sleep( 5000 ) ;
        
        gotoCreditCardsLandingPage() ;
        outstandingDues = extractCurrentDuesOnCard() ;
        gotoLast30DaysCCTxnListPage() ;
        
        List<CCTxnEntry> txnEntries = parseTxnList() ;
        for( CCTxnEntry entry : txnEntries ) {
            log.debug( entry ) ;
        }
        
        browser.postDataToServer( this.serverAddress, 
                                  "/Ledger/CCTxnEntries", 
                                  txnEntries ) ;
        
        Thread.sleep( 5000 ) ;
    }
    
    private void gotoCreditCardsLandingPage() {
        
        WebElement myAccountsNavLink = browser.findByLinkText( "MY ACCOUNTS" ) ;
        myAccountsNavLink.click() ;
        
        WebElement ccPageLink = browser.findById( "Credit-Cards" ) ;
        ccPageLink.click() ;
    }
    
    private float extractCurrentDuesOnCard() {
        
        String amtSpanXPath = "//*[@id=\"credit-1\"]/div[1]/div[3]/div[2]/p/span" ;
        WebElement amtSpan = browser.findElement( By.xpath( amtSpanXPath ) ) ;
        String amtStr = amtSpan.getText().substring( 2 ) ;
        return -1*Float.parseFloat( amtStr ) ;
    }
    
    private void gotoLast30DaysCCTxnListPage() {
        
        browser.waitForElement( By.linkText( "Last 30 Days Transactions" ) ) ;
        browser.findByLinkText( "Last 30 Days Transactions" )
               .click() ;
        browser.waitForElement( By.id( "Caption4567923" ) ) ;
    }
    
    private List<CCTxnEntry> parseTxnList() throws Exception {
        
        String txnTBodyXPath = "//*[@id=\"Redeem\"]/tbody" ;
        String txnRowsXPath = txnTBodyXPath + "/tr[@class=\"listgreyrow\"]" ;
        
        List<CCTxnEntry> ccTxns = new ArrayList<>() ;
        List<WebElement> rows = browser.findElements( By.xpath( txnRowsXPath ) ) ;
        log.debug( rows.size() + " transactions found." ) ;
        
        for( int rowNum=2; rowNum<=rows.size()+1; rowNum++ ) {
            String rowXPath = txnTBodyXPath + "/tr[" + rowNum + "]" ;
            CCTxnEntry entry = new CCTxnEntry() ;
            
            entry.setCreditCardNumber( creditCardNumber ) ;
            entry.setBalance( outstandingDues ) ;
            entry.setValueDate( CCTxnEntry.SDF.parse( getTxnAttr( rowXPath, 1 ) ) );
            entry.setRemarks( getTxnAttr( rowXPath, 3 ) ) ;
            
            String amtStr = getTxnAttr( rowXPath, 4 ) ;
            boolean isDebit = amtStr.endsWith( "Dr." ) ;
            amtStr = amtStr.replace( ",", "" ) ;
            amtStr = amtStr.substring( 0, amtStr.length()-4 ) ;

            Float amt = Float.parseFloat( amtStr ) ;
            if( isDebit ) {
                entry.setAmount( -amt ) ;
            }
            else{
                entry.setAmount( amt ) ;
            }
            
            ccTxns.add( entry ) ;
        }
        return ccTxns ;
    }

    private String getTxnAttr( String rowXPath, int colNum ) {
        String cellXPath = rowXPath + "/td[" + colNum + "]" ;
        WebElement element = browser.findElement( By.xpath( cellXPath ) ) ;
        return element.getText() ;
    }
}

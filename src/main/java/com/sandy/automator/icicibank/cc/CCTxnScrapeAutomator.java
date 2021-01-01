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

public class CCTxnScrapeAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( CCTxnScrapeAutomator.class ) ;
    
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
        
        List<CCTxnEntry> txnEntries = new ArrayList<>() ;
        
        parseTransactionsFromStatement( "Last Statement", txnEntries ) ;
        
        browser.clickElement( By.name( "Action.BACK" ) ) ;
        
        parseTransactionsFromStatement( "Current Statement", txnEntries ) ;
        
        log.debug( "All CC transactions " );
        for( CCTxnEntry entry : txnEntries ) {
            log.debug( "\t" + entry ) ;
        }
        
        browser.postDataToServer( this.serverAddress, 
                                  "/Ledger/CCTxnEntries", 
                                  txnEntries ) ;

        Thread.sleep( 5000 ) ;
    }
    
    private void gotoCreditCardsLandingPage() {
        
        WebElement myAccountsNavLink = browser.findByLinkText( "CARDS & LOANS" ) ;
        myAccountsNavLink.click() ;
        
        WebElement ccPageLink = browser.findById( "Credit-Cards" ) ;
        ccPageLink.click() ;
    }
    
    private float extractCurrentDuesOnCard() {
      
        String amtSpanXPath = "//*[@id=\"credit-1\"]/div[1]/div[2]/div[2]/p/span" ;
        WebElement amtSpan = browser.findElement( By.xpath( amtSpanXPath ) ) ;
        
        int creditDebitMultiplier = -1 ;
        String amtStr = amtSpan.getText().substring( 2 ) ;
        if( amtStr.contains( "Cr" ) ) {
            creditDebitMultiplier = 1 ;
        }
        amtStr = amtStr.substring( 0, amtStr.length()-3 ).trim() ;
        return creditDebitMultiplier*Float.parseFloat( amtStr ) ;
    }
    
    private void  parseTransactionsFromStatement( 
            String linkText, List<CCTxnEntry> globalEntries ) 
        throws Exception {
        
        WebElement stmtLink = browser.findByLinkText( linkText ) ;
        stmtLink.click() ;
        
        List<CCTxnEntry> txnEntries = new ArrayList<>() ;
        
        String txnTableXPath = "//table[@id=\"Redeem\"]" ;
        
        List<WebElement> txnTables = browser.findElements( By.xpath( txnTableXPath ) ) ;
        
        // The first table is the reward programs table.. ignore it and start
        // from the second table onwards. Each table starting from the second
        // table represents the transactions for a particular card
        for( int i=1; i<txnTables.size(); i++ ) {
            String tableXPath = "(" + txnTableXPath + ")[" + (i+1) + "]" ;
            List<CCTxnEntry> entries = parseTxnTable( tableXPath ) ;
            if( !entries.isEmpty() ) {
                txnEntries.addAll( entries ) ;
            }
        }
        
        if( !txnEntries.isEmpty() ) {
            globalEntries.addAll( txnEntries ) ;
        }
    }
    
    private List<CCTxnEntry> parseTxnTable( String tableXPath ) 
        throws Exception {

        String baseTRXPath = tableXPath + "/tbody/tr[@class=\"listgreyrow\"]" ;

        List<WebElement> txnRows = browser.findElements( By.xpath( baseTRXPath ) ) ;
        List<CCTxnEntry> entries = new ArrayList<>() ;
        
        for( int i=0; i<txnRows.size(); i++ ) {
            String rowXPath = "(" + baseTRXPath + ")[" + (i+1) + "]" ;
            CCTxnEntry entry = parseTxnEntry( rowXPath ) ;
            
            if( entry != null ) {
                entries.add( entry ) ;
            }
        }
        
        return entries ;
    }

    private CCTxnEntry parseTxnEntry( String rowXPath ) throws Exception {
        
        CCTxnEntry entry = new CCTxnEntry() ;
        
        entry.setCreditCardNumber( creditCardNumber ) ;
        entry.setBalance( outstandingDues ) ;
        entry.setValueDate( CCTxnEntry.SDF.parse( getTxnAttr( rowXPath, 1 ) ) );
        entry.setRemarks( enrichRemark( getTxnAttr( rowXPath, 3 ) ) ) ;
        
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

        return entry ;
    }
    
    private String enrichRemark( String rawRemark ) {
        if( rawRemark.endsWith( ", IN" ) ) {
            int lastIndex = rawRemark.lastIndexOf( ',' ) ;
            lastIndex = rawRemark.lastIndexOf( ',', lastIndex-1 ) ;
            rawRemark = rawRemark.substring( 0, lastIndex ) ;
        }
        return rawRemark ;
    }

    private String getTxnAttr( String rowXPath, int colNum ) {
        String cellXPath = rowXPath + "/td[" + colNum + "]" ;
        WebElement element = browser.findElement( By.xpath( cellXPath ) ) ;
        return element.getText().trim() ;
    }
}

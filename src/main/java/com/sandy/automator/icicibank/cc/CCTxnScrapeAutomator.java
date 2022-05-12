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
    
    private float totalOutstandingDues = 0 ;
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
        
        // At this point, we are at the credit card landing page which has
        // two tabs. One for the primary card which is selected by default and
        // the other one is for the Sapphiro card. We need to extact details
        // of both cards one after another.
        
        List<CCTxnEntry> txnEntries = new ArrayList<>() ;
        
        log.debug( "\nProcessing Rubyx credit card\n" ) ;
        processCreditCard( 1, txnEntries ) ;
        
        log.debug( "\nProcessing Sapphiro credit card\n" ) ;
        processCreditCard( 2, txnEntries ) ;
        
        log.debug( "\nExtraction complete" );
        for( CCTxnEntry entry : txnEntries ) {
            entry.setBalance( totalOutstandingDues ) ;
        }
        
        log.debug( "" ) ;
        log.debug( "Total outstanding dues  = " + totalOutstandingDues ) ;
        log.debug( "Number of entries       = " + txnEntries.size() ) ;

        browser.postDataToServer( this.serverAddress, 
                                  "/Ledger/CCTxnEntries", 
                                  txnEntries ) ;

        Thread.sleep( 5000 ) ;
    }
    
    private void gotoCreditCardsLandingPage() 
        throws Exception{
        
        String cssSel1 = ".d-flex:nth-child(4) > .f-11" ;
        WebElement ccDropdown = browser.findElement( By.cssSelector( cssSel1 ) ) ;
        ccDropdown.click() ;
        
        String cssSel2 = ".d-flex:nth-child(4) .mr-15:nth-child(1) > .d-flex:nth-child(2) > .font-12" ;
        WebElement ccLandingLink = browser.findElement( By.cssSelector( cssSel2 ) ) ;
        ccLandingLink.click() ;
        
        Thread.sleep( 2000 ) ;
    }
    
    private void processCreditCard( int tabNumber, List<CCTxnEntry> txnEntries ) 
        throws Exception {
        
        // Structure of CC page
        //  - Each CC tab has an ID of "credit-tab-" + tabNumber
        //  - On clicking each tab, a div by the id "pb" + (tabNumber -1) 
        //    gets activated, which contains further details of the card dues
        //
        // Example - first tab has id "credit-tab-1" and the div id is "pb0"
        String tabId = "credit-tab-" + tabNumber ;
        String divId = "pb" + (tabNumber -1 ) ;
        
        WebElement tabLink = browser.findById( tabId ) ;
        tabLink.click() ;
        
        int   creditLimit          = extractCreditLimit( divId ) ;
        float currentOutstanding   = extractCurrentDues( divId ) ;
        float availableCreditLimit = extractAvailableCreditLimit( divId ) ;
        
        log.debug( "Credit limit           = " + creditLimit ) ;
        log.debug( "Oustanding dues        = " + currentOutstanding ) ;
        log.debug( "Credit limit available = " + availableCreditLimit ) ;
        log.debug( "" ) ;
        
        totalOutstandingDues += currentOutstanding ;
        
        parseTransactionsFromStatement( tabNumber, "Last Statement", txnEntries ) ;
        browser.clickElement( By.name( "Action.BACK" ) ) ;
        browser.waitForElement( By.id( tabId ) ) ;
        tabLink = browser.findById( tabId ) ;
        tabLink.click() ;
       
        parseTransactionsFromStatement( tabNumber, "Current Statement", txnEntries ) ;
        browser.clickElement( By.name( "Action.BACK" ) ) ;
        browser.waitForElement( By.id( tabId ) ) ;
        tabLink = browser.findById( tabId ) ;
        tabLink.click() ;
    }
    
    private int extractCreditLimit( String divId ) {
        
        String creditLimitXPath = "//*[@id=\"" + divId + "\"]/h6" ;
        WebElement element = browser.findElement( By.xpath( creditLimitXPath ) ) ;
        
        String elementText = element.getText() ;
        String amtText = elementText.substring( "Credit limit INR".length() )
                                    .trim() ;
        
        return Integer.parseInt( amtText ) ;
    }
    
    private float extractCurrentDues( String divId ) {
      
        String amtSpanXPath = "//*[@id=\"" + divId + "\"]/div[3]/div[2]/p/span" ;
        WebElement amtSpan = browser.findElement( By.xpath( amtSpanXPath ) ) ;
        
        String amtStr = amtSpan.getText().substring( 2 ) ;
        amtStr = amtStr.replace( ",", "" ) ;
        
        boolean isCredit = false ;
        if( amtStr.endsWith( " Cr" ) ) {
            isCredit = true ;
            amtStr = amtStr.substring( 0, amtStr.length()-3 ).trim() ;
        }
        
        float currentDues = Float.parseFloat( amtStr ) ;
        if( !isCredit ) {
            currentDues = -1*currentDues ; 
        }
        
        return currentDues ;
    }
    
    private float extractAvailableCreditLimit( String divId ) {
        
        String amtSpanXPath = "//*[@id=\"" + divId + "\"]/div[3]/div[3]/p/span" ;
        WebElement amtSpan = browser.findElement( By.xpath( amtSpanXPath ) ) ;
        
        String amtStr = amtSpan.getText().substring( 2 ) ;
        amtStr = amtStr.replace( ",", "" ) ;
        return Float.parseFloat( amtStr ) ;
    }
    
    private void  parseTransactionsFromStatement( 
            int tabNumber, String linkText, List<CCTxnEntry> globalEntries ) 
        throws Exception {
        
        String linkXPath = "//*[@id=\"credit-" + tabNumber + "\"]/div[5]/a[text()='" + linkText + "']" ;
        
        WebElement stmtLink = browser.findElement( By.xpath( linkXPath ) ) ;
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
                log.debug( entry ) ;
            }
        }
        
        return entries ;
    }

    private CCTxnEntry parseTxnEntry( String rowXPath ) throws Exception {
        
        CCTxnEntry entry = new CCTxnEntry() ;
        
        entry.setCreditCardNumber( creditCardNumber ) ;
        entry.setValueDate( CCTxnEntry.SDF.parse( getTxnAttr( rowXPath, 1 ) ) );
        entry.setRemarks( enrichRemark( getTxnAttr( rowXPath, 2 ), 
                                        getTxnAttr( rowXPath, 3 ) ) ) ;
        
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
    
    private String enrichRemark( String txnRefNum, String rawRemark ) {
        if( rawRemark.endsWith( ", IN" ) ) {
            int lastIndex = rawRemark.lastIndexOf( ',' ) ;
            lastIndex = rawRemark.lastIndexOf( ',', lastIndex-1 ) ;
            rawRemark = rawRemark.substring( 0, lastIndex ) ;
        }
        return "[" + txnRefNum + "] " + rawRemark ;
    }

    private String getTxnAttr( String rowXPath, int colNum ) {
        String cellXPath = rowXPath + "/td[" + colNum + "]" ;
        WebElement element = browser.findElement( By.xpath( cellXPath ) ) ;
        return element.getText().trim() ;
    }
}

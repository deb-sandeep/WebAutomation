package com.sandy.automator.icicibank.cc;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.icicibank.ICICIBankSiteAutomator ;

public class CCEntryParseUCAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( CCEntryParseUCAutomator.class ) ;
    
    private Browser browser = null ;
    private ICICIBankSiteAutomator siteAutomator = null ;
    private SiteCredential cred = null ;
    private String serverAddress = null ;
    
    @Override
    public boolean canExecuteForCredential( SiteCredential cred ) {
        return cred.getBooleanAttribute( "hasCC" ) ;
    }
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.cred = cred ;
        this.siteAutomator = ( ICICIBankSiteAutomator )getSiteAutomator() ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        gotoLast30DaysCCTxnListPage() ;
        Thread.sleep( 5000 ) ;
    }
    
    private void gotoLast30DaysCCTxnListPage() {
        
        WebElement myAccountsNavLink = browser.findByLinkText( "MY ACCOUNTS" ) ;
        myAccountsNavLink.click() ;
        
        WebElement ccPageLink = browser.findById( "Credit-Cards" ) ;
        ccPageLink.click() ;
        
        browser.waitForElement( By.linkText( "Last 30 Days Transactions" ) ) ;
        WebElement last30dayTxnsLink = browser.findByLinkText( "Last 30 Days Transactions" ) ;
        last30dayTxnsLink.click() ;
    }
}

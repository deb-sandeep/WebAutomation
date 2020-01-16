package com.sandy.automator.icicidirect;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class ICICIDirectSiteAutomator extends SiteAutomator {

    private static final Logger log = Logger.getLogger( ICICIDirectSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://secure.icicidirect.com/IDirectTrading/Customer/login.aspx" ;
    
    private Browser browser = null ;

    @Override
    public void execute( Browser browser ) throws Exception {
        this.browser = browser ;
        for( SiteCredential cred : credentials ) {
            loginUser( cred ) ;
            for( UseCaseAutomator ucAutomator : useCaseAutomators ) {
                try {
                    log.debug( "Executing use case automator " + ucAutomator.getUcId() );
                    ucAutomator.execute( cred, browser ) ;
                }
                catch( Exception e ) {
                    log.error( "Exception in usecase automator " + 
                               ucAutomator.getUcId(), e ) ;
                }
            }
            logoutUser( cred ) ;
        }
    }

    public void gotoSection( SiteSection section ) {
        log.debug( "Going to section - " + section.getSelector() ) ;
        browser.clickLink( section.getSelector() ) ;
    }
    
    private void loginUser( SiteCredential cred ) {
        
        log.debug( "Logging in user - " + cred.getUserName() ) ;
        browser.get( SITE_LOGIN_URL ) ;
        
        WebElement userIdTF = browser.findElement( By.id( "txtUserId" ) ) ;
        WebElement passwordTF = browser.findElement( By.id( "txtPass" ) ) ;
        WebElement dobTF = browser.findElement( By.id( "txtDOB" ) ) ;
        WebElement submitBtn = browser.findElement( By.id( "lbtLogin" ) ) ;
        
        userIdTF.sendKeys( cred.getUserName() ) ;
        passwordTF.sendKeys( cred.getPassword() ) ;
        dobTF.sendKeys( cred.getAttribute( "dob" ) ) ;
        
        submitBtn.click() ;
    }
    
    private void logoutUser( SiteCredential cred ) {
        
        log.debug( "Logging out current user" ) ;
        // Bubble the exception if Logout link is not found. 
        browser.clickLink( By.linkText( "Logout" ) ) ;
    }
}

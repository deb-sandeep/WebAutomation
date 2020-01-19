package com.sandy.automator.icicidirect;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class ICICIDirectSiteAutomator extends SiteAutomator {

    private static final Logger log = Logger.getLogger( ICICIDirectSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://secure.icicidirect.com/IDirectTrading/Customer/login.aspx" ;
    
    public void gotoSection( SiteSection section ) {
        log.debug( "Going to section - " + section.getSelector() ) ;
        browser.clickLink( section.getSelector() ) ;
    }
    
    public void loginUser( SiteCredential cred ) throws Exception {
        
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
    
    public void logoutUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging out current user" ) ;
        // Bubble the exception if Logout link is not found. 
        browser.clickLink( By.linkText( "Logout" ) ) ;
    }
}

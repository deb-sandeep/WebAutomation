package com.sandy.automator.icicidirect.newsite;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class ICICIDirectNewSiteAutomator extends SiteAutomator {

    private static final Logger log = Logger.getLogger( ICICIDirectNewSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://secure.icicidirect.com/customer/login" ;
    
    public void gotoSection( NewSiteSection section ) {
        log.debug( "  # Going to section - " + section.getSelector() ) ;
        browser.clickLink( section.getSelector() ) ;
    }
    
    public void loginUser( SiteCredential cred ) throws Exception {
        
        log.debug( ">> Populating login credentials." ) ;
        browser.get( SITE_LOGIN_URL ) ;
        
        // Give some time before we fill the login creds. This is to allow 
        // the user to close any message popups.
        log.debug( "  # Waiting for 5 seconds" ) ;
        Thread.sleep( 5000 ) ;
        
        log.debug( "  # Populating uid, password and dob" ) ;
        WebElement userIdTF   = browser.findElement( By.id( "txtuid" ) ) ;
        WebElement passwordTF = browser.findElement( By.id( "txtPass" ) ) ;
        WebElement dobTF      = browser.findElement( By.id( "txtDOB" ) ) ;
        WebElement submitBtn  = browser.findElement( By.id( "btnlogin" ) ) ;
        
        userIdTF.clear() ;
        userIdTF.sendKeys( cred.getUserName() ) ;
        
        passwordTF.clear() ;
        passwordTF.sendKeys( cred.getPassword() ) ;
        
        dobTF.clear() ;
        dobTF.sendKeys( cred.getAttribute( "dob" ) ) ;

        log.debug( "  # Clicking login button" ) ;
        submitBtn.click() ;
        
        // After login, give some time for user to close any message popups
        // that the site might want to display.
        log.debug( "  # Waiting for 7 seconds" ) ;
        Thread.sleep( 7000 ) ;
    }
    
    public void logoutUser( SiteCredential cred ) throws Exception {
        
        // Bubble the exception if Logout link is not found. 
        WebElement logoutBtn = browser.findElement( By.className( "logout-btn" ) ) ;
        logoutBtn.click() ;
    }
}

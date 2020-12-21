package com.sandy.automator.etrade ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class ETradeSiteAutomator extends SiteAutomator {

    private static final Logger log = Logger.getLogger( ETradeSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://us.etrade.com/e/t/user/login" ;
    
    public void loginUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging in user - " + cred.getUserName() ) ;
        browser.get( SITE_LOGIN_URL ) ;
        
        Thread.sleep( 4000 ) ;
        
        WebElement userNameTF = browser.findElement( By.name( "USER" ) ) ;
        WebElement passwordTF = browser.findElement( By.name( "PASSWORD" ) ) ;
        
        log.debug( "Sending user name = " + cred.getUserName() ) ;
        typeKeys( userNameTF, cred.getUserName() ) ; 
        Thread.sleep( 4000 ) ;

        log.debug( "Sending password = " + cred.getPassword() ) ;
        typeKeys( passwordTF, cred.getPassword() ) ;
        Thread.sleep( 4000 ) ;

        WebElement logonBtn = browser.findById( "logon_button" ) ;
        logonBtn.click() ;

        Thread.sleep( 40000 ) ;
    }
    
    private void typeKeys( WebElement webElement, String text ) 
    	throws Exception {
    	
    	for( int i=0; i<text.length(); i++ ) {
    		char c = text.charAt( i ) ;
    		webElement.sendKeys( "" + c ) ;
    		Thread.sleep( 200 ) ;
    	}
    }
    
    public void logoutUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging out current user" ) ;
        WebElement logOutBtn = browser.findByLinkText( "Log Off" ) ;
        logOutBtn.click() ;
    }
}

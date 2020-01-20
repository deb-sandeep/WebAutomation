package com.sandy.automator.icicibank ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class ICICIBankSiteAutomator extends SiteAutomator {

    private static final Logger log = Logger.getLogger( ICICIBankSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://infinity.icicibank.com/corp/AuthenticationController?FORMSGROUP_ID__=AuthenticationFG&__START_TRAN_FLAG__=Y&FG_BUTTONS__=LOAD&ACTION.LOAD=Y&AuthenticationFG.LOGIN_FLAG=1&BANK_ID=ICI" ;
    
    public void loginUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging in user - " + cred.getUserName() ) ;
        browser.get( SITE_LOGIN_URL ) ;
        
        WebElement loginByIDPwdBtn = browser.findElement( By.id( "user-id-goahead" ) ) ;
        loginByIDPwdBtn.click() ;
        
        browser.waitForElement( By.id( "AuthenticationFG.USER_PRINCIPAL" ) ) ;
        WebElement uidTF = browser.findById( "AuthenticationFG.USER_PRINCIPAL" ) ;
        WebElement pwdTF = browser.findById( "AuthenticationFG.ACCESS_CODE" ) ;
        WebElement goBtn = browser.findById( "VALIDATE_CREDENTIALS1" ) ;
        
        uidTF.sendKeys( cred.getUserName() ) ;
        pwdTF.sendKeys( cred.getPassword() ) ;
        goBtn.click() ;
    }
    
    public void logoutUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging out current user" ) ;
        WebElement logOutBtn = browser.findById( "HREF_Logout" ) ;
        logOutBtn.click() ;
    }
}

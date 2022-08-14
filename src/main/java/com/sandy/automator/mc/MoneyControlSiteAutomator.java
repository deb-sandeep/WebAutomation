package com.sandy.automator.mc ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class MoneyControlSiteAutomator extends SiteAutomator {

    static final Logger log = Logger.getLogger( MoneyControlSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://accounts.moneycontrol.com/mclogin/?d=2undefined&cpurl=https://www.moneycontrol.com/" ;
    
    private static final String UID_TF_XPATH = "(//*[@id=\"email\"])[2]" ;
    private static final String PWD_TF_XPATH = "(//*[@id=\"pwd\"])[2]" ;
    private static final String SUBMIT_ID    = "ACCT_LOGIN_SUBMIT" ;
    
    public void loginUser( SiteCredential cred ) throws Exception {
        
        browser.get( SITE_LOGIN_URL ) ;
        
        browser.waitForElement( By.xpath( UID_TF_XPATH ) ) ;
        
        WebElement uidTF = browser.findElement( By.xpath( UID_TF_XPATH ) ) ;
        WebElement pwdTF = browser.findElement( By.xpath( PWD_TF_XPATH ) ) ;
        WebElement goBtn = browser.findById( SUBMIT_ID ) ;
        
        uidTF.sendKeys( cred.getUserName() ) ;
        pwdTF.sendKeys( cred.getPassword() ) ;
        goBtn.click() ;
        
        browser.waitForElement( By.xpath( "//*[@class=\"usr_nm\"]" ) ) ;
        
        if( browser.elementExists( By.id( "wzrk-cancel" ) ) ) {
            WebElement pushCancelBtn = browser.findById( "wzrk-cancel" ) ;
            pushCancelBtn.click() ;
        } 
    }
    
    public void logoutUser( SiteCredential cred ) throws Exception {
        
        WebElement userIconMenuLink = browser.findElement( By.className( "userlink" ) ) ;
        userIconMenuLink.click() ;
        
        browser.waitForElement( By.linkText( "Logout" ) ) ;
        WebElement logoutLink = browser.findByLinkText( "Logout" ) ;
        logoutLink.click() ;
    }
}

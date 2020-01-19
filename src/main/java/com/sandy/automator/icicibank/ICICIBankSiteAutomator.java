package com.sandy.automator.icicibank ;

import org.apache.log4j.Logger ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class ICICIBankSiteAutomator extends SiteAutomator {

    private static final Logger log = Logger.getLogger( ICICIBankSiteAutomator.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "XXX" ;
    
    public void loginUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging in user - " + cred.getUserName() ) ;
        browser.get( SITE_LOGIN_URL ) ;
    }
    
    public void logoutUser( SiteCredential cred ) throws Exception {
        
        log.debug( "Logging out current user" ) ;
    }
}

package com.sandy.automator.icicibank.cc;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import org.apache.log4j.Logger ;

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
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.cred = cred ;
        this.siteAutomator = ( ICICIBankSiteAutomator )getSiteAutomator() ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
    }
}

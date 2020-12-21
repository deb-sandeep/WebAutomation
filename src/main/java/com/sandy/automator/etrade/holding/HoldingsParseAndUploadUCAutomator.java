package com.sandy.automator.etrade.holding;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import org.apache.log4j.Logger ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class HoldingsParseAndUploadUCAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( HoldingsParseAndUploadUCAutomator.class ) ;
    
    private Browser browser = null ;
    private String serverAddress = null ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        this.browser = browser ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        Thread.sleep( 1000 ) ;
        Thread.sleep( 5000 ) ;
    }
}

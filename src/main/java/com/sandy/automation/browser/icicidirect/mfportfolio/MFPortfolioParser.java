package com.sandy.automation.browser.icicidirect.mfportfolio;

import org.apache.log4j.Logger ;

import com.sandy.automation.browser.icicidirect.Cred ;
import com.sandy.automation.browser.icicidirect.Module ;

public class MFPortfolioParser extends Module {
    
    private static final Logger log = Logger.getLogger( MFPortfolioParser.class ) ;
    
    @Override
    public void execute() throws Exception {
        for( Cred cred : super.credentials ) {
            parsePortfolioFor( cred ) ;
            log.debug( "Temp : Breaking after one user. Remove this later." ) ;
            break ;
        }
    }
    
    private void parsePortfolioFor( Cred cred ) throws Exception {
        parent.loginUser( cred ) ;
        Thread.sleep( 7000 ) ;
        parent.logoutUser() ;
    }
}

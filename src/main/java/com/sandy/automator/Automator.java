package com.sandy.automator;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.builder.AutomatorBuilder ;

public class Automator {
    
    private static final Logger log = Logger.getLogger( Automator.class ) ;
    
    private File workspacePath = null ;
    private boolean enableHeadless = false ;
    private boolean enableServerCommunication = true ;
    private List<SiteAutomator> siteAutomators = new ArrayList<>() ;
    
    public void setWorkspacePath( File path ) {
        this.workspacePath = path ;
    }
    
    public File getWorkspacePath() {
        return this.workspacePath ;
    }

    public void setEnableHeadless( boolean value ) {
        this.enableHeadless = value ;
    }
    
    public boolean isHeadlessEnabled() {
        return this.enableHeadless ;
    }

    public void setEnableServerCommunication( boolean value ) {
        this.enableServerCommunication = value ;
    }
    
    public boolean isServerCommunicationEnabled() {
        return enableServerCommunication ;
    }

    public void initialize() throws Exception {
        log.debug( "Initializing Automator" ) ;
        AutomatorBuilder builder = new AutomatorBuilder() ;
        builder.buildAutomator( this ) ;
    }
    
    public void addSiteAutomator( SiteAutomator siteAutomator ) {
        siteAutomators.add( siteAutomator ) ;
    }
    
    public void execute() throws Exception {
        log.debug( "Executing automator." ) ;
        if( siteAutomators.isEmpty() ) {
            log.info( "No active site automator found." ) ;
        }
        else {
            Browser browser = new Browser( this ) ;
            for( SiteAutomator siteAutomator : siteAutomators ) {
                try {
                    log.info( "Executing site automator - " + 
                              siteAutomator.getSiteId() ) ;
                    
                    siteAutomator.execute( browser ) ;
                }
                catch( Exception e ) {
                    log.error( "Exception in site Automator " + 
                               siteAutomator.getSiteId(), e );
                }
            }
            browser.quit() ;
        }
    }
    
    public static void main( String[] args ) {
        try {
            Automator automator = new Automator() ;
            automator.initialize() ;
            automator.execute() ;
        }
        catch( Exception e ) {
            log.error( "Automator exited due to an exception.", e ) ;
        }
    }
}

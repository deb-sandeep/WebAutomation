package com.sandy.automator.core;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.log4j.Logger ;

import com.sandy.automator.Automator ;
import com.sandy.automator.core.cfg.SiteCredential ;

public class SiteAutomator implements Configurable {
    
    private static final Logger log = Logger.getLogger( SiteAutomator.class ) ;
    
    public static final String CAPITALYST_SERVER_ADDRESS_KEY = "capitalystServer" ;
    public static final String DEFAULT_SERVER_ADDRESS = "localhost:8080" ;

    private Map<String, UseCaseAutomator> ucAutomatorMap = new HashMap<>() ;
    
    protected List<UseCaseAutomator> useCaseAutomators = new ArrayList<>() ;
    protected Automator parentAutomator = null ;
    protected String siteId = null ;
    protected PropertiesConfiguration config = null ;
    protected List<SiteCredential> credentials = new ArrayList<>() ;
    protected Browser browser = null ;
    
    public void setParentAutomator( Automator automator ) {
        this.parentAutomator = automator ;
    }
    public Automator getParentAutomator() {
        return this.parentAutomator ;
    }
    
    public String getSiteId() {
        return siteId ;
    }
    public void setSiteId( String siteId ) {
        this.siteId = siteId ;
    }
    
    public void addUseCaseAutomator( UseCaseAutomator automator ) {
        this.useCaseAutomators.add( automator ) ;
        this.ucAutomatorMap.put( automator.getUcId(), automator ) ;
    }
    
    protected UseCaseAutomator getUseCaseAutomator( String id ) {
        return this.ucAutomatorMap.get( id ) ;
    }
    
    public void setCredentials( List<SiteCredential> creds ) {
        if( creds != null && !creds.isEmpty() ) {
            credentials.addAll( creds ) ;
        }
    }
    
    public SiteCredential getCredential( String individualName ) {
        SiteCredential retVal = null ;
        for( SiteCredential cred : credentials ) {
            if( cred.getIndividualName().equals( individualName ) ) {
                retVal = cred ;
                break ;
            }
        }
        return retVal ;
    }

    @Override
    public void setPropertiesConfiguation( PropertiesConfiguration config ) {
        this.config = config ;
    }
    
    /*
     * Default behavior:
     *    If no credentials are present
     *      Execute each use case executor sequentially
     *    Else if credentials are present
     *      Do nothing
     */
    public void execute( Browser browser ) throws Exception {
        this.browser = browser ;
        if( credentials.isEmpty() ) {
            doUserAgnosticExecution() ;
        }
        else {
            doUserSpecificExecution() ;
        }
    }
    
    private void doUserAgnosticExecution() throws Exception {
        
        for( UseCaseAutomator ucAutomator : useCaseAutomators ) {
            try {
                log.debug( "Executing use case automator " + ucAutomator.getUcId() );
                ucAutomator.execute( null, browser ) ;
            }
            catch( Exception e ) {
                log.error( "Exception in usecase automator " + 
                           ucAutomator.getUcId(), e ) ;
            }
        }
    }
    
    private void doUserSpecificExecution() throws Exception {
        
        for( SiteCredential cred : credentials ) {
            for( UseCaseAutomator ucAutomator : useCaseAutomators ) {
                try {
                    if( ucAutomator.canExecuteForCredential( cred ) ) {
                        loginUser( cred ) ;
                        log.debug( "Executing use case automator " + ucAutomator.getUcId() );
                        ucAutomator.execute( cred, browser ) ;
                        logoutUser( cred ) ;
                    }
                    else {
                        log.debug( "Use case automator - " + 
                                   ucAutomator.getUcId() + 
                                   " can't execute for user " + 
                                   cred.getIndividualName() ) ;
                    }
                }
                catch( Exception e ) {
                    log.error( "Exception in usecase automator " + 
                               ucAutomator.getUcId(), e ) ;
                }
            }
        }
    }
    
    protected void loginUser( SiteCredential cred ) 
        throws Exception {
    } ;
    
    protected void logoutUser( SiteCredential cred ) 
        throws Exception {
    } ;
}

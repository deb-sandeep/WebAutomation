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
    private Map<String, SiteCredential> individualCredMap = new HashMap<>() ;
    
    protected List<SiteCredential> credentials = new ArrayList<>() ;
    protected List<UseCaseAutomator> useCaseAutomators = new ArrayList<>() ;
    protected Automator parentAutomator = null ;
    protected String siteId = null ;
    protected PropertiesConfiguration config = null ;
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
            credentials.clear() ;
            credentials.addAll( creds ) ;
            for( SiteCredential cred : creds ) {
                individualCredMap.put( cred.getIndividualName(), cred ) ;
            }
        }
    }
    
    public SiteCredential getCredential( String individualName ) {
        return individualCredMap.get( individualName ) ;
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
        
        Map<String, List<UseCaseAutomator>> userUCAutomatorMap = new HashMap<>() ;
        
        for( SiteCredential cred : credentials ) {
            for( UseCaseAutomator ucAutomator : useCaseAutomators ) {
                if( ucAutomator.canExecuteForCredential( cred ) ) {
                    List<UseCaseAutomator> automators = null ;
                    automators = userUCAutomatorMap.get( cred.getIndividualName() ) ;
                    if( automators == null ) {
                        automators = new ArrayList<>() ;
                        userUCAutomatorMap.put( cred.getIndividualName(), automators ) ;
                    }
                    automators.add( ucAutomator ) ;
                }
                else {
                    log.debug( "Use case automator - " + ucAutomator.getUcId() + 
                               " can't execute for " + cred.getIndividualName() ) ;
                }
            }
        }
        
        List<UseCaseAutomator> automators = null ;
        SiteCredential cred = null ;
        
        for( String individualName : userUCAutomatorMap.keySet() ) {
            
            cred = getCredential( individualName ) ;
            
            log.info( "Executing for user " + cred.getUserName() ) ;
            log.info( "---------------------------------------" ) ;
            if( cred.isEnabled() ) {
                try {
                    automators = userUCAutomatorMap.get( individualName ) ;
                    
                    log.info( "\n>> Logging in user " + cred.getUserName() ) ;
                    loginUser( cred ) ;
                    
                    for( UseCaseAutomator ucAutomator : automators ) {
                        ucAutomator.execute( cred, browser ) ;
                    }
                    
                    log.info( "\n>> Logging out user " + cred.getUserName() + "\n" ) ;
                    logoutUser( cred ) ;
                }
                catch( Exception e ) {
                    log.error( "Exception in usecase automator.", e ) ;
                }
            }
            else {
                log.info( "  User " + cred.getUserName() + " is not enabled.\n" ) ;
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

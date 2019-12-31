package com.sandy.automation.browser.icicidirect;

import java.io.File ;
import java.net.URL ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.configuration2.Configuration ;
import org.apache.commons.configuration2.XMLConfiguration ;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder ;
import org.apache.commons.configuration2.builder.fluent.Parameters ;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver ;
import org.apache.log4j.Logger ;

import com.sandy.automation.browser.AutomationBase ;
import com.sandy.automation.util.ConfigUtils ;

public class ICICIDirectAutomation extends AutomationBase {
    
    private static final Logger log = Logger.getLogger( ICICIDirectAutomation.class ) ;
    
    private static final String DTD_ENTITY_ID = 
            "-//Sandy Web Automation.//DTD ICICIDirect Creds 1.0//EN" ;
    
    private static final String DTD_LOCAL_RESOURCE_PATH = 
            "/dtd/icici-creds.dtd" ;
    
    private static final String LOCAL_CFG = 
            "icicidirect-creds.xml" ;
    
    private List<Cred> credentials = new ArrayList<>() ;

    public ICICIDirectAutomation() throws Exception {}
    
    @Override
    public Configuration loadAppConfig() throws Exception {
        return ConfigUtils.loadPropertiesConfig( "icici-direct" ) ;
    }
    
    public void execute() {
        try {
            loadCredentials() ;
            for( Cred cred : credentials ) {
                runScrapeAutomationFor( cred ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error in automation.", e ) ;
        }
        finally {
            if( webDriver != null ) {
                webDriver.quit() ;
            }
        }
    }
    
    private void runScrapeAutomationFor( Cred cred ) 
        throws Exception {
        
        log.debug( "Running scrape for " + cred.getUserName() ) ;
        super.loginUser( cred ) ;
        Thread.sleep( 5000 ) ; 
        super.logoutUser() ;
    }
    
    private void loadCredentials() throws Exception {
        
        XMLConfiguration credsConfig = getXMLConfiguration() ;
        List<String> userIds = credsConfig.getList( String.class, 
                                                    "creds.cred.user-id" ) ;
        for( int i=0; i<userIds.size(); i++ ) {
            String subConfigPath = "creds.cred(" + i + ")" ;
            Configuration config = credsConfig.configurationAt( subConfigPath ) ;
            credentials.add(  new Cred(
                ( String )config.getProperty( "user-id" ),
                ( String )config.getProperty( "password" ),
                ( String )config.getProperty( "dob" )
            ) ) ;
        }
        
        if( credentials.isEmpty() ) {
            throw new Exception( "No credentials found." ) ;
        }
    }
    
    private XMLConfiguration getXMLConfiguration() throws Exception {
        
        Parameters params = new Parameters() ;
        DefaultEntityResolver resolver = new DefaultEntityResolver() ;
        URL dtdURL = getClass().getResource( DTD_LOCAL_RESOURCE_PATH ) ;
        
        resolver.registerEntityId( DTD_ENTITY_ID, dtdURL ) ;

        File credsCfg = new File( System.getProperty( "user.home" ), LOCAL_CFG ) ;
        if( !credsCfg.exists() ) {
            throw new Exception( "ICICIDirect user credentials not found." ) ;
        }
        
        FileBasedConfigurationBuilder<XMLConfiguration> builder =
            new FileBasedConfigurationBuilder<>( XMLConfiguration.class )
                .configure( params.xml()
                                  .setFile( credsCfg )
                                  .setEntityResolver( resolver )
                                  .setValidating( true ) ) ;

        return builder.getConfiguration() ;
    }
    
    public static void main( String[] args ) throws Exception {
        log.debug( "Starting ICICIDirect web automation..." ) ;
        ICICIDirectAutomation driver = new ICICIDirectAutomation() ;
        driver.execute() ;
    }
}

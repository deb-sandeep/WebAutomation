package com.sandy.automation.browser.icicidirect;

import java.io.File ;
import java.net.URL ;

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
    
    private static final String SITE_URL = "https://secure.icicidirect.com/IDirectTrading/Customer/login.aspx" ;
    private static final String DTD_ENTITY_ID = "-//Sandy Web Automation.//DTD ICICIDirect Creds 1.0//EN" ;
    private static final String DTD_LOCAL_RESOURCE_PATH = "/dtd/icici-creds.dtd" ;
    private static final String LOCAL_CFG = "icicidirect-creds.xml" ;
    
    private XMLConfiguration credsConfig = null ;

    public ICICIDirectAutomation() throws Exception {}
    
    @Override
    public Configuration loadAppConfig() throws Exception {
        
        this.credsConfig = getXMLConfiguration() ;
        return ConfigUtils.loadPropertiesConfig( "icici-direct" ) ;
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
    
    public void execute() {
        try {
            //webDriver.get( SITE_URL ) ;
        }
        catch( Exception e ) {
            log.error( "Error in automation.", e ) ;
        }
        finally {
            webDriver.close() ;
        }
    }
    
    public static void main( String[] args ) throws Exception {
        log.debug( "Starting ICICIDirect web automation..." ) ;
        ICICIDirectAutomation driver = new ICICIDirectAutomation() ;
        driver.execute() ;
    }
}

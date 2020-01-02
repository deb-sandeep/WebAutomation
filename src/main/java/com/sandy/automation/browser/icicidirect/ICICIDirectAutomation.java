package com.sandy.automation.browser.icicidirect;

import java.io.File ;
import java.net.URL ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.concurrent.TimeUnit ;

import org.apache.commons.configuration2.Configuration ;
import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.commons.configuration2.XMLConfiguration ;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder ;
import org.apache.commons.configuration2.builder.fluent.Parameters ;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebElement ;

import com.sandy.automation.browser.AutomationBase ;
import com.sandy.automation.util.ConfigUtils ;

public class ICICIDirectAutomation extends AutomationBase {
    
    private static final Logger log = Logger.getLogger( ICICIDirectAutomation.class ) ;
    
    private static final String SITE_LOGIN_URL = 
            "https://secure.icicidirect.com/IDirectTrading/Customer/login.aspx" ;
    
    private static final String DTD_ENTITY_ID = 
            "-//Sandy Web Automation.//DTD ICICIDirect Creds 1.0//EN" ;
    
    private static final String DTD_LOCAL_RESOURCE_PATH = 
            "/dtd/icicidirect.dtd" ;
    
    private static final String LOCAL_CFG = 
            "icicidirect.xml" ;
    
    private List<Cred> credentials = new ArrayList<>() ;
    private List<ModuleConfig> moduleConfigs = new ArrayList<>() ;

    public ICICIDirectAutomation() throws Exception {}
    
    public static void main( String[] args ) throws Exception {
        log.debug( "Starting ICICIDirect web automation..." ) ;
        ICICIDirectAutomation driver = new ICICIDirectAutomation() ;
        driver.execute() ;
    }
    
    @Override
    public PropertiesConfiguration loadAppConfig() throws Exception {
        return ConfigUtils.loadPropertiesConfig( "icici-direct" ) ;
    }
    
    public void execute() {
        try {
            log.debug( "Loading credentials." ) ;
            loadCredentials() ;
            
            log.debug( "Loading modules." ) ;
            loadModules() ;
            
            if( getActiveModuleCount() > 0 ) {
                super.initializeWebDriver() ;
                for( ModuleConfig moduleCfg : moduleConfigs ) {
                    if( moduleCfg.isEnabled() ) {
                        log.debug( "Executing module - " + moduleCfg.getId() ) ;
                        loadAndExecuteModule( moduleCfg ) ;
                    }
                }
            }
            else {
                log.debug( "There are no active modules." ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error in automation.", e ) ;
        }
        finally {
            super.quitWebDriver() ;
            try {
                TimeUnit.SECONDS.sleep( 2 ) ;
            }
            catch( InterruptedException e ) {
            }
        }
    }
    
    public void loginUser( Cred cred ) {
        
        log.debug( "Logging in user - " + cred.getUserName() ) ;
        webDriver.get( SITE_LOGIN_URL ) ;
        
        WebElement userIdTF = webDriver.findElement( By.id( "txtUserId" ) ) ;
        WebElement passwordTF = webDriver.findElement( By.id( "txtPass" ) ) ;
        WebElement dobTF = webDriver.findElement( By.id( "txtDOB" ) ) ;
        WebElement submitBtn = webDriver.findElement( By.id( "lbtLogin" ) ) ;
        
        userIdTF.sendKeys( cred.getUserName() ) ;
        passwordTF.sendKeys( cred.getPassword() ) ;
        dobTF.sendKeys( cred.getDob() ) ;
        
        submitBtn.click() ;
    }
    
    public void logoutUser() {
        
        log.debug( "Logging out current user" ) ;
        // Bubble the exception if Logout link is not found. 
        clickLink( "Logout" ) ;
    }
    
    public void gotoSection( SiteSection section ) {
        log.debug( "Going to section - " + section.getLinkText() ) ;
        clickLink( section.getLinkText() ) ;
    }
    
    // ------------------ PRIVATE SECTION -----------------------------------
    
    private void clickLink( String linkText ) 
            throws IllegalStateException {
        
        List<WebElement> links = null ;
        
        links = webDriver.findElements( By.linkText( linkText ) ) ;
        if( !links.isEmpty() ) {
            WebElement link = links.get( 0 ) ;
            link.click() ;
        }
        else {
            String msg = "Link '" + linkText + "' not found on page." ;
            throw new IllegalStateException( msg ) ;
        }
    }
    
    private int getActiveModuleCount() {
        int activeCount = 0 ;
        for( ModuleConfig cfg : moduleConfigs ) {
            if( cfg.isEnabled() ) {
                activeCount++ ;
            }
        }
        return activeCount ;
    }
    
    private void loadModules() {
        
        String[] moduleNames = config.getStringArray( "modules" ) ;
        for( String moduleName : moduleNames ) {
            Configuration cfg = config.subset( "modules." + moduleName ) ;
            ModuleConfig module = new ModuleConfig(
                moduleName,
                cfg.getString( "class" ),
                cfg.getBoolean( "enabled" ),
                cfg.getString( "description" )
            ) ;
            moduleConfigs.add( module ) ;
        }
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
                ( String )config.getProperty( "individual-name" ),
                ( String )config.getProperty( "password" ),
                ( String )config.getProperty( "dob" )
            ) ) ;
        }
        
        if( credentials.isEmpty() ) {
            throw new Exception( "No credentials found." ) ;
        }
    }
    
    @SuppressWarnings( "unchecked" )
    private Module loadModule( ModuleConfig moduleCfg ) 
        throws Exception {
        
        Class<? extends Module> moduleCls = null ;
        moduleCls = (Class<? extends Module>) Class.forName( moduleCfg.getClassName() ) ;
        return moduleCls.getDeclaredConstructor().newInstance() ;
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
    
    private void loadAndExecuteModule( ModuleConfig moduleCfg ) {
        try {
            Module module = loadModule( moduleCfg ) ;
            module.setParent( this ) ;
            module.setCredentials( credentials ) ;
            
            module.execute() ;
        }
        catch( Exception e ) {
            log.debug( "Exception executing module.", e ) ;
        }
    }
}

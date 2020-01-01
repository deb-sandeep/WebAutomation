package com.sandy.automation.browser;

import java.io.File ;
import java.util.HashMap ;
import java.util.Map ;

import org.apache.commons.configuration2.CombinedConfiguration ;
import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.chrome.ChromeDriver ;
import org.openqa.selenium.chrome.ChromeOptions ;

import com.sandy.automation.util.ConfigUtils ;

public abstract class AutomationBase {
    
    private static final Logger log = Logger.getLogger( AutomationBase.class ) ;

    private File workspaceDir = null ;
    private File downloadsDir = null ;
    
    protected CombinedConfiguration config = null ;
    protected WebDriver webDriver = null ;
    
    protected AutomationBase() throws Exception {
        loadConfig() ;
    }

    private void loadConfig() throws Exception {
        log.debug( "Loading configuration." ) ;
        
        config = new CombinedConfiguration() ;

        PropertiesConfiguration baseConfig = ConfigUtils.loadPropertiesConfig( "web-automation" ) ;
        PropertiesConfiguration appConfig = loadAppConfig() ;
        
        if( appConfig != null ) {
            config.addConfiguration( appConfig ) ;
        }
        config.addConfiguration( baseConfig ) ;
    }
    
    protected PropertiesConfiguration loadAppConfig() throws Exception {
        log.debug( "No app specific configuration found." ) ;
        return null ;
    }
    
    protected void initializeWebDriver() {
        ChromeOptions options = prepareChromeOptions() ;
        webDriver = new ChromeDriver( options ) ;
    }
    
    private ChromeOptions prepareChromeOptions() {
        
        Map<String, Object> prefs = new HashMap<String, Object>() ;
        prefs.put( "profile.default_content_settings.popups", 0 ) ;
        prefs.put( "download.default_directory", getDownloadsDir().getAbsolutePath() ) ;
        
        ChromeOptions options = new ChromeOptions() ;
        options.setExperimentalOption( "prefs", prefs ) ;
        if( config.getBoolean( "chrome.enableHeadless" ) ) {
            options.addArguments( "--headless" ) ;
        }
        return options ;
    }
    
    protected void addAppSpecificChromeOptions( ChromeOptions options ) {}
    
    protected File getWorkspaceDir() {
        if( this.workspaceDir == null ) {
            String path = config.getString( "project.workspacePath" ) ;
            if( path == null ) {
                path = "/home/sandeep/temp" ;
            }
            
            workspaceDir = new File( path ) ;
            if( !workspaceDir.exists() ) {
                workspaceDir.mkdirs() ;
            }
        }
        return workspaceDir ;
    }
    
    protected File getDownloadsDir() {
        if( this.downloadsDir == null ) {
            downloadsDir = new File( getWorkspaceDir(), "downloads" ) ;
            if( !downloadsDir.exists() ) {
                downloadsDir.mkdirs() ;
            }
        }
        return downloadsDir ;
    }

    protected void cleanDownloadsFolder() throws Exception {
        log.debug( "Cleaning workspace directory." ) ;
        FileUtils.cleanDirectory( getDownloadsDir() ) ;
    }
}
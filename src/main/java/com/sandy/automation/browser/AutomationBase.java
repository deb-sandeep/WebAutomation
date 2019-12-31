package com.sandy.automation.browser;

import java.io.File ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.configuration2.CombinedConfiguration ;
import org.apache.commons.configuration2.Configuration ;
import org.apache.commons.configuration2.builder.fluent.Configurations ;
import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.chrome.ChromeDriver ;
import org.openqa.selenium.chrome.ChromeOptions ;

import com.sandy.automation.browser.icicidirect.Cred ;

public abstract class AutomationBase {
    
    private static final Logger log = Logger.getLogger( AutomationBase.class ) ;

    private static final String SITE_LOGIN_URL = 
            "https://secure.icicidirect.com/IDirectTrading/Customer/login.aspx" ;
    
    private File workspaceDir = null ;
    private File downloadsDir = null ;
    
    protected CombinedConfiguration config = null ;
    protected WebDriver webDriver = null ;
    
    protected AutomationBase() throws Exception {
        loadConfig() ;
        ChromeOptions options = prepareChromeOptions() ;
        webDriver = new ChromeDriver( options ) ;
    }

    private void loadConfig() throws Exception {
        Configurations configurations = new Configurations() ;
        Configuration baseConfig = configurations.properties( 
                    getClass().getResource( "/web-automation.properties" )
                              .toURI()
                              .toURL() ) ;
        
        
        config = new CombinedConfiguration() ;
        Configuration appConfig = loadAppConfig() ;
        if( appConfig != null ) {
            config.addConfiguration( appConfig ) ;
        }
        config.addConfiguration( baseConfig ) ;
    }
    
    protected Configuration loadAppConfig() throws Exception {
        return null ;
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
        FileUtils.cleanDirectory( getDownloadsDir() ) ;
    }

    protected void loginUser( Cred cred ) {
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
    
    protected void logoutUser() {
        
        log.debug( "Logging out current user" ) ;
        List<WebElement> logoutLinks = webDriver.findElements( By.linkText( "Logout" ) ) ;
        if( !logoutLinks.isEmpty() ) {
            WebElement logoutLink = logoutLinks.get( 0 ) ;
            logoutLink.click() ;
        }
        else {
            log.error( "No logout link found. Can't logout" ) ;
        }
    }
}
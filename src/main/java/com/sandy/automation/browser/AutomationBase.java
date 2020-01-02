package com.sandy.automation.browser;

import java.io.File ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.concurrent.TimeUnit ;

import org.apache.commons.configuration2.CombinedConfiguration ;
import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.TimeoutException ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.chrome.ChromeDriver ;
import org.openqa.selenium.chrome.ChromeOptions ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.automation.util.ConfigUtils ;

import okhttp3.MediaType ;
import okhttp3.OkHttpClient ;
import okhttp3.Request ;
import okhttp3.RequestBody ;
import okhttp3.Response ;

public abstract class AutomationBase {
    
    private static final Logger log = Logger.getLogger( AutomationBase.class ) ;
    
    public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" ) ; 
    public static final String CAPITALYST_SERVER_CFG_KEY = "capitalystServer.address" ;

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
    
    public void initializeWebDriver() {
        ChromeOptions options = prepareChromeOptions() ;
        webDriver = new ChromeDriver( options ) ;
    }
    
    public void quitWebDriver() {
        if( webDriver != null ) {
            webDriver.close() ;
            webDriver.quit() ;
        }
    }
    
    public File downloadFile( By selector ) throws Exception {
        
        log.debug( "Downloading file." ) ;
        WebElement downloadElement = null ;
        File downloadDir = getDownloadsDir() ;
        
        cleanDownloadsFolder() ;
        
        downloadElement = webDriver.findElement( selector ) ;
        downloadElement.click() ;
        
        Thread.sleep( 2000 ) ;
        
        File[] downloadedFiles = downloadDir.listFiles() ;
        if( downloadedFiles.length == 0 ) {
            throw new TimeoutException( "File download timedout.." ) ;
        }
        
        File downloadedFile = downloadedFiles[0] ;
        long startLen = downloadedFile.length() ;
        long endLen = startLen ;
        log.debug( "\t\tDownloading file " + downloadedFile.getName() ) ;
        do {
            Thread.sleep( 500 ) ;
            endLen = downloadedFile.length() ;
        }
        while( startLen != endLen ) ;
        log.debug( "\t\tDownload completed." ) ;
        
        return downloadedFile ;
    }
    
    public String postDataToServer( String serverAddressCfgKey,
                                  String endpointPath, 
                                  Object postData )
            throws Exception {
            
        log.debug( "Posting data to server." ) ;
        
        ObjectMapper objMapper = new ObjectMapper() ;
        String json = objMapper.writeValueAsString( postData ) ;
        String serverAddress = config.getString( serverAddressCfgKey ) ;
        
        String url = "http://" + serverAddress + endpointPath ;
        log.debug( "\tURL = " + url ) ;
        
        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout( 60, TimeUnit.SECONDS )
                                .readTimeout( 60, TimeUnit.SECONDS )
                                .build() ;
        
        RequestBody body = RequestBody.create( JSON, json ) ;
        Request request = new Request.Builder()
                                     .url( url )
                                     .post( body )
                                     .build() ;
        Response response = null ;
        String responseBody = null ;
        try {
            response = client.newCall( request ).execute() ;
            responseBody = response.body().string() ;
            log.debug( "\t\tResponse from server - " + response.code() ) ;
            log.debug( "\t\t\t" + responseBody ) ;
        }
        finally {
            if( response != null ) {
                response.body().close() ;
            }
        }
        return responseBody ;
    }

    // ----------------- PROTECTED SECTION -----------------------------------
    
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
    
    // ----------------- PRIVATE SECTION ------------------------------------
    
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
}
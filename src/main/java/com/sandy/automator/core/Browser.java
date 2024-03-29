package com.sandy.automator.core;

import java.io.File ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Set ;
import java.util.concurrent.TimeUnit ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;
import org.json.JSONObject ;
import org.openqa.selenium.By ;
import org.openqa.selenium.TimeoutException ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.chrome.ChromeDriver ;
import org.openqa.selenium.chrome.ChromeOptions ;
import org.openqa.selenium.support.ui.ExpectedConditions ;
import org.openqa.selenium.support.ui.WebDriverWait ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.automator.Automator ;

import okhttp3.MediaType ;
import okhttp3.OkHttpClient ;
import okhttp3.Request ;
import okhttp3.RequestBody ;
import okhttp3.Response ;

public class Browser {
    
    private static final Logger log = Logger.getLogger( Browser.class ) ;
    
    public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" ) ; 
    
    private WebDriver delegate = null ;
    private boolean serverCommEnabled = true ;
    private File downloadsDir = null ;
    
    public Browser( Automator automator ) { 
        downloadsDir = getDownloadsDir( automator ) ;
        serverCommEnabled = automator.isServerCommunicationEnabled() ;
        ChromeOptions options = prepareChromeOptions( automator ) ;
        delegate = new ChromeDriver( options ) ;
    }
    
    public void clickLink( By selector ) throws IllegalStateException {
        
        waitForElement( selector ) ;
        
        List<WebElement> links = null ;
        links = delegate.findElements( selector ) ;
        if( !links.isEmpty() ) {
            WebElement link = links.get( 0 ) ;
            link.click() ;
        }
        else {
            String msg = "Link '" + selector + "' not found on page." ;
            throw new IllegalStateException( msg ) ;
        }
    }
    
    public boolean elementExists( By selector ) {
        if( delegate.findElements( selector ).size() != 0 ) {
            return true ;
        } 
        return false ;
    }
    
    public void waitForElement( By selector ) {
        WebDriverWait wait = new WebDriverWait( delegate, 10 ) ;
        wait.until( ExpectedConditions.elementToBeClickable( selector ) ) ;
    }
    
    public void clickElement( By elementRef ) {
        delegate.findElement( elementRef ).click() ;
    }
    
    public void cleanDownloadsFolder() throws Exception {
        FileUtils.cleanDirectory( downloadsDir ) ;
    }
    
    public File downloadFile( By selector ) throws Exception {
        
        WebElement downloadElement = null ;
        
        cleanDownloadsFolder() ;
        
        downloadElement = findElement( selector ) ;
        downloadElement.click() ;
        
        Thread.sleep( 2000 ) ;
        
        File[] downloadedFiles = downloadsDir.listFiles() ;
        if( downloadedFiles.length == 0 ) {
            throw new TimeoutException( "File download timedout.." ) ;
        }
        
        File downloadedFile = downloadedFiles[0] ;
        long startLen = downloadedFile.length() ;
        long endLen = startLen ;
        
        log.debug( "\tDownloading file " + downloadedFile.getName() ) ;
        do {
            Thread.sleep( 500 ) ;
            endLen = downloadsDir.listFiles()[0].length() ;
            log.debug( "\t  Total len = " + startLen + ". Current len = " + endLen ) ;
        }
        while( startLen != endLen ) ;
        
        log.debug( "\t\tDownload completed." ) ;
        
        // This is required because chrome downloads file with an extension
        // .crdownload till the time the download is in progress, after
        // which the .crdownload is reverted.
        downloadedFile = downloadsDir.listFiles()[0] ;
        
        return downloadedFile ;
    }
    
    public String postDataToServer( String serverAddress,
                                    String endpointPath, 
                                    Object postData )
        throws Exception {
        
        if( !serverCommEnabled ) {
            log.info( "Server communication is disabled." ) ;
            return "Server communication is disabled by configuration" ;
        }
        
        ObjectMapper objMapper = new ObjectMapper() ;
        String json = objMapper.writeValueAsString( postData ) ;
        
        String url = "http://" + serverAddress + endpointPath ;
        log.debug( "\tPosting to " + url ) ;
        
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
            
            try {
                JSONObject jsonObj = new JSONObject( responseBody ) ;
                responseBody = jsonObj.toString( 2 ) ;
            }
            catch( Exception e ) {
                // Do nothing. The response is not a JSON object, so 
                // print it as it is.
            }
            
            log.debug( "\t  Response from server - " + response.code() ) ;
            log.debug( "\t    Msg = " + responseBody ) ;
        }
        finally {
            if( response != null ) {
                response.body().close() ;
            }
        }
        return responseBody ;
    }
    
    public WebElement findById( String id ) {
        return findElement( By.id( id ) ) ;
    }
    
    public WebElement findByLinkText( String linkText ) {
        return findElement( By.linkText( linkText ) ) ;
    }

    // --------------- Private utility methods -------------------------------
    private File getDownloadsDir( Automator automator ) {
        if( downloadsDir == null ) {
            downloadsDir = new File( Automator.getWorkspacePath(), "downloads" ) ;
            if( !downloadsDir.exists() ) {
                downloadsDir.mkdirs() ;
            }
        }
        return downloadsDir ;
    }
    
    private ChromeOptions prepareChromeOptions( Automator automator ) {
        
        Map<String, Object> prefs = new HashMap<String, Object>() ;
        prefs.put( "profile.default_content_settings.popups", 0 ) ;
        prefs.put( "download.default_directory", downloadsDir.getAbsolutePath() ) ;
        
        ChromeOptions options = new ChromeOptions() ;
        options.setExperimentalOption( "prefs", prefs ) ;
        if( automator.isHeadlessEnabled() ) {
            options.addArguments( "--headless" ) ;
        }
        return options ;
    }
    
    // -----------------------------------------------------------------------
    // Delegation methods. These should not be changed unless decoration 
    // is required
    public void get( String url ) { delegate.get( url ) ; }
    public String getCurrentUrl() { return delegate.getCurrentUrl() ; }
    public String getTitle() { return delegate.getTitle() ; }
    public List<WebElement> findElements( By by ) { return delegate.findElements( by ) ; }
    public WebElement findElement( By by ) { return delegate.findElement( by ) ; }
    public String getPageSource() { return delegate.getPageSource() ; }
    public void close() { delegate.close() ; }
    public Set<String> getWindowHandles() { return delegate.getWindowHandles() ; }
    public String getWindowHandle() { return delegate.getWindowHandle() ; }
    public WebDriver.TargetLocator switchTo() { return delegate.switchTo() ; }
    public WebDriver.Navigation navigate() { return delegate.navigate() ; }
    public WebDriver.Options manage() { return delegate.manage() ; }

    public void quit() { 
        delegate.close() ;
        delegate.quit() ; 
    }
}

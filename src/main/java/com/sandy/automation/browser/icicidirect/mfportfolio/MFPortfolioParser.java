package com.sandy.automation.browser.icicidirect.mfportfolio;

import java.io.File ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.support.ui.Select ;

import com.sandy.automation.browser.icicidirect.Cred ;
import com.sandy.automation.browser.icicidirect.Module ;
import com.sandy.automation.browser.icicidirect.SiteSection ;

public class MFPortfolioParser extends Module {
    
    private static final Logger log = Logger.getLogger( MFPortfolioParser.class ) ;
    
    private static final By CSV_DN_SELECTOR = By.cssSelector(  "img[title='Summary:Export To CSV']" ) ;
    private static final By HOLDING_TYPE_SELECT = By.id( "DDL_Status" ) ;
    private static final By VIEW_BTN = By.id( "Viewbut" ) ;
    
    @Override
    public void execute() throws Exception {
        for( Cred cred : super.credentials ) {
            browser.loginUser( cred ) ;
            parsePortfolioFor( cred ) ;
            browser.logoutUser() ;
            log.debug( "Temp : Breaking after one user. Remove this later." ) ;
            break ;
        }
    }
    
    private void parsePortfolioFor( Cred cred ) throws Exception {
        browser.gotoSection( SiteSection.TI ) ;
        browser.gotoSection( SiteSection.TI_PS ) ;
        browser.gotoSection( SiteSection.TI_PS_MF ) ;
        
        showAllHoldings() ;
        
        File csvFile = browser.downloadFile( CSV_DN_SELECTOR ) ;
        String contents = FileUtils.readFileToString( csvFile ) ;
        log.debug( contents ) ;
    }
    
    private void showAllHoldings() throws Exception {
        
        log.debug( "Showing all mutual fund holdings" ) ;
        
        WebElement element = null ;
        WebDriver webDriver = browser.getWebDriver() ;
        
        browser.waitForElement( HOLDING_TYPE_SELECT ) ;
        element = webDriver.findElement( HOLDING_TYPE_SELECT ) ;
        Select dropdown = new Select( element ) ;
        dropdown.selectByVisibleText( "All" ) ;
        
        element = webDriver.findElement( VIEW_BTN ) ;
        element.click() ;
        Thread.sleep( 1000 ) ;
    }
}

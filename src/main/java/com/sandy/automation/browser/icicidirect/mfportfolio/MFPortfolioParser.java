package com.sandy.automation.browser.icicidirect.mfportfolio;

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
    
    private static final String CSV_SELECTOR = "img[title='Summary:Export To CSV']" ;
    
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
    }
    
    private void showAllHoldings() throws Exception {
        
        log.debug( "Showing all mutual fund holdings" ) ;
        
        WebElement element = null ;
        WebDriver webDriver = browser.getWebDriver() ;
        
        element = webDriver.findElement( By.id( "DDL_Status" ) ) ;
        Select dropdown = new Select( element ) ;
        dropdown.selectByVisibleText( "All" ) ;
        
        element = webDriver.findElement( By.id( "Viewbut" ) ) ;
        element.click() ;
        Thread.sleep( 1000 ) ;
    }
}

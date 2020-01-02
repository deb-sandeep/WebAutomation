package com.sandy.automation.browser.icicidirect.mfportfolio;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.openqa.selenium.By ;
import org.openqa.selenium.WebDriver ;
import org.openqa.selenium.WebElement ;
import org.openqa.selenium.support.ui.Select ;

import com.sandy.automation.browser.AutomationBase ;
import com.sandy.automation.browser.icicidirect.Cred ;
import com.sandy.automation.browser.icicidirect.Module ;
import com.sandy.automation.browser.icicidirect.SiteSection ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

public class MFPortfolioParser extends Module {
    
    private static final Logger log = Logger.getLogger( MFPortfolioParser.class ) ;
    
    private static final By CSV_DN_SELECTOR = By.cssSelector(  "img[title='Summary:Export To CSV']" ) ;
    private static final By HOLDING_TYPE_SELECT = By.id( "DDL_Status" ) ;
    private static final By VIEW_BTN = By.id( "Viewbut" ) ;
    
    private CsvParser csvParser = null ;
    
    @Override
    public void execute() throws Exception {
        
        csvParser = getCsvParser() ;
        
        for( Cred cred : super.credentials ) {
            browser.loginUser( cred ) ;
            processPortfolioFor( cred ) ;
            browser.logoutUser() ;
            log.debug( "Temp : Breaking after one user. Remove this later." ) ;
            break ;
        }
    }
    
    private void processPortfolioFor( Cred cred ) throws Exception {
        
        List<MutualFundAsset> mfAssets = null ;
        File csvFile = null ;
        
        browser.gotoSection( SiteSection.TI ) ;
        browser.gotoSection( SiteSection.TI_PS ) ;
        browser.gotoSection( SiteSection.TI_PS_MF ) ;
        
        showAllHoldings() ;
        
        csvFile = browser.downloadFile( CSV_DN_SELECTOR ) ;
        mfAssets = parseMFAssets( cred.getIndividualName(), csvFile ) ;
        
        browser.postDataToServer( AutomationBase.CAPITALYST_SERVER_CFG_KEY, 
                                  "/MutualFund/Portfolio", 
                                  mfAssets ) ;
        
    }
    
    private List<MutualFundAsset> parseMFAssets( String ownerName, File csvFile ) {
        
        List<MutualFundAsset> assets = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;
        
        for( int i=1; i<csvFileContents.size(); i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            MutualFundAsset mfAsset = buildMFAsset( ownerName, tupule ) ;
            log.debug( mfAsset ) ;
            assets.add( mfAsset ) ;
        }
        
        return assets ;
    }
    
    private CsvParser getCsvParser() {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( 
            "Scheme",
            "Category",
            "Sub Category",
            "Units Held",
            "Average Cost Price",
            "Value At Cost",
            "Last Recorded NAV *",
            "Value at NAV",
            "Profit/ Loss",
            "Profit/ Loss %"
        ) ;
        
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }
    
    private MutualFundAsset buildMFAsset( String ownerName, String[] tupule ) {
        MutualFundAsset asset = new MutualFundAsset() ;
        
        asset.setOwnerName( ownerName ) ;
        asset.setScheme( tupule[0] ) ;
        asset.setCategory( tupule[1] ) ;
        asset.setSubCategory( tupule[2] ) ;
        asset.setUnitsHeld( Float.parseFloat( tupule[3] ) ) ;
        asset.setAvgCostPrice( Float.parseFloat( tupule[4] ) ) ;
        asset.setValueAtCost( Float.parseFloat( tupule[5] ) ) ;
        asset.setLastRecordedNav( Float.parseFloat( tupule[6] ) ) ;
        asset.setValueAtNav(  Float.parseFloat( tupule[7] ) ) ;
        asset.setProfitLossAmt( Float.parseFloat( tupule[8] ) ) ;
        asset.setProfitLossPct( Float.parseFloat( tupule[9] ) ) ;

        return asset ;
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

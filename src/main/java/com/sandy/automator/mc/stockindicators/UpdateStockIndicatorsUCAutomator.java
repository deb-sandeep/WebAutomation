package com.sandy.automator.mc.stockindicators ;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.InputStream ;
import java.util.Date ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory ;
import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.ObjectSerializer ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.mc.stockindicators.cfg.LastRunState ;
import com.sandy.automator.mc.stockindicators.cfg.StockConfig ;
import com.sandy.automator.mc.stockindicators.cfg.StockIndicatorAutomationConfig ;

public class UpdateStockIndicatorsUCAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( UpdateStockIndicatorsUCAutomator.class ) ;
    
    private static final String LAST_STATE_PERSIST_KEY = "MCStockInd-LastRunState" ;
    
    private Browser browser = null ;
    
    private String  serverAddress = null ;
    
    private StockIndicatorAutomationConfig saConfig = null ;
    private StockIndicatorsExtractor extractor = new StockIndicatorsExtractor() ;
    
    private LastRunState lastRunState = null ;
    private ObjectSerializer serializer = new ObjectSerializer() ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        log.debug( "Executing MCStockMetaUploadAutomator" ) ;
        
        this.browser = browser ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        log.debug( "  Loading configuration" ) ;
        this.saConfig = loadProperties() ;
        
        log.debug( "  Loading last run state" ) ;
        loadLastRunState() ;
        
        int numDone = 0 ;
        int totalNum = this.saConfig.getStockCfgs().size() ;
        
        for( StockConfig cfg : this.saConfig.getStockCfgs() ) {
            
            int tryCount = 0 ;
            boolean tryAgain = true ;
            
            if( !lastRunState.getProcessedISINList().contains( cfg.getIsin() ) ) {
                
                // If we faced an exception on the first try, keep trying 
                // till the tryAgain flag is true.
                while( tryAgain ) {
                    try {
                        tryCount++ ;
                        updateStockIndicators( cfg ) ;
                        
                        log.debug( "       " + (numDone+1) + " done. " + 
                                   ( totalNum - numDone - 1 ) + " remaining." );
                        
                        Thread.sleep( (int)(Math.random()*4000) ) ;
                        tryAgain = false ;
                    }
                    catch( Exception e ) {
                        // We try again once in case of a failure.
                        log.error( "    ERROR in processing.", e ) ;
                        tryAgain = tryCount < 2 ;
                    }
                }
            }
            numDone++ ;
        }
    }
    
    private StockIndicatorAutomationConfig loadProperties() throws Exception {
        
        ObjectMapper mapper = null ; 
        InputStream is = null ;
        StockIndicatorAutomationConfig config = null ;
        
        mapper = new ObjectMapper( new YAMLFactory() ) ; 
        mapper.findAndRegisterModules() ;
        
        is = getClass().getResourceAsStream( "/mc-stock-details.yaml" ) ;
        config = mapper.readValue( is, StockIndicatorAutomationConfig.class ) ;

        return config ;
    }
    
    private void loadLastRunState() throws Exception {
        
        lastRunState = ( LastRunState )serializer.deserializeObj( LAST_STATE_PERSIST_KEY ) ;
        
        if( lastRunState == null ) {
            lastRunState = new LastRunState() ;
        }
        
        if( !DateUtils.isSameDay( new Date(), lastRunState.getSaveDate() ) ) {
            serializer.deserializeObj( LAST_STATE_PERSIST_KEY ) ;
            lastRunState = new LastRunState() ;
        }
        
        if( saConfig.isRunFresh() ) {
            serializer.deserializeObj( LAST_STATE_PERSIST_KEY ) ;
            lastRunState = new LastRunState() ;
        }
    }
    
    private void saveLastRunState( String lastProcessedISIN )
        throws Exception {
        
        lastRunState.setSaveDate( new Date() ) ;
        lastRunState.getProcessedISINList().add( lastProcessedISIN ) ;
        serializer.serializeObj( lastRunState, LAST_STATE_PERSIST_KEY ) ;
    }
    
    private void updateStockIndicators( StockConfig cfg ) 
        throws Exception {
        
        StockIndicators indicators = null ;
        
        log.debug( "\n  Updating indicators for " + cfg.getSymbolNSE() ) ;
        indicators = extractor.extractAttributes( browser, cfg ) ;
        
        browser.postDataToServer( serverAddress, 
                                  "/Equity/Master/MCStockIndicators", 
                                  indicators ) ;
        
        saveLastRunState( cfg.getIsin() ) ;
    }
}

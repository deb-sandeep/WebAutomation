package com.sandy.automator.mc.stockcharacteristics ;

import static com.sandy.automator.core.SiteAutomator.CAPITALYST_SERVER_ADDRESS_KEY ;
import static com.sandy.automator.core.SiteAutomator.DEFAULT_SERVER_ADDRESS ;

import java.io.InputStream ;

import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory ;
import com.sandy.automator.core.Browser ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.SiteCredential ;
import com.sandy.automator.mc.stockcharacteristics.cfg.StockAttributesUpdateConfig ;
import com.sandy.automator.mc.stockcharacteristics.cfg.StockConfig ;

public class UpdateStockAttributesUCAutomator extends UseCaseAutomator {

    private static final Logger log = Logger.getLogger( UpdateStockAttributesUCAutomator.class ) ;
    
    private Browser browser = null ;
    
    @SuppressWarnings( "unused" )
    private String  serverAddress = null ;
    
    private StockAttributesUpdateConfig saConfig = null ;
    private StockAttributesExtractor extractor = new StockAttributesExtractor() ;
    
    @Override
    public void execute( SiteCredential cred, Browser browser )
            throws Exception {
        
        log.debug( "Executing MapISINAutomator" ) ;
        
        this.browser = browser ;
        this.serverAddress = config.getString( CAPITALYST_SERVER_ADDRESS_KEY, 
                                               DEFAULT_SERVER_ADDRESS ) ;
        
        log.debug( "  Loading configuration" ) ;
        this.saConfig = loadProperties() ;
        
        for( StockConfig cfg : this.saConfig.getStockCfgs() ) {
            
            log.debug( "\n  Updating attributes for " + cfg.getSymbolNSE() ) ;
            updateStockCharacteristics( cfg ) ;
        }
    }
    
    private StockAttributesUpdateConfig loadProperties() throws Exception {
        
        ObjectMapper mapper = null ; 
        InputStream is = null ;
        StockAttributesUpdateConfig config = null ;
        
        mapper = new ObjectMapper( new YAMLFactory() ) ; 
        mapper.findAndRegisterModules() ;
        
        is = getClass().getResourceAsStream( "/mc-stock-details.yaml" ) ;
        config = mapper.readValue( is, StockAttributesUpdateConfig.class ) ;

        return config ;
    }
    
    private void updateStockCharacteristics( StockConfig cfg ) 
        throws Exception {
        
        StockAttributes attributes = null ;
        attributes = extractor.extractAttributes( browser, cfg ) ;
    }
}

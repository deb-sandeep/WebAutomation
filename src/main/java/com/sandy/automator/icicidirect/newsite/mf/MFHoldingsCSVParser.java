package com.sandy.automator.icicidirect.newsite.mf;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.automator.icicidirect.vo.mf.MutualFundAsset ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

public class MFHoldingsCSVParser {
    
    static final Logger log = Logger.getLogger( MFHoldingsCSVParser.class ) ;


    public List<MutualFundAsset> parseMFHoldings( String ownerName, File csvFile ) {
        
    
        CsvParser csvParser = getMFHoldingsCsvParser() ;
        List<MutualFundAsset> assets = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;
        
        for( int i=1; i<csvFileContents.size(); i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            MutualFundAsset mfAsset = buildMFAsset( ownerName, tupule ) ;
            assets.add( mfAsset ) ;
        }
        
        return assets ;
    }
    
    private CsvParser getMFHoldingsCsvParser() {
        
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
        asset.setScheme( tupule[0].replace( "  ", " " ) ) ;
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
}

package com.sandy.automator.icicidirect.newsite.equity;

import java.io.File ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.automator.icicidirect.vo.equity.EquityHolding ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

import static com.sandy.automator.core.Util.parseFloatAmt ;

public class EquityHoldingsCSVParser {
    
    static final Logger log = Logger.getLogger( EquityHoldingsCSVParser.class ) ;

    public List<EquityHolding> parseEquityHoldings( String ownerName, 
                                                    File csvFile,
                                                    List<String> equityFilters ) {
        
        CsvParser csvParser = getHoldingSummaryCsvParser() ;
        
        List<EquityHolding> assets = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;
        
        for( int i=1; i<csvFileContents.size(); i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            EquityHolding holding = buildEquityHolding( ownerName, tupule ) ;
            
            if( equityFilters.isEmpty() ) {
                assets.add( holding ) ;
            }
            else if( equityFilters.contains( holding.getSymbolIcici() ) ) {
                assets.add( holding ) ;
            }
        }
        return assets ;
    }
    
    private CsvParser getHoldingSummaryCsvParser() {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( 
            "Stock Symbol",
            "Company Name",
            "ISIN Code",
            "Qty",
            "Average Cost Price",
            "Current Market Price",
            "Realized Profit / Loss"
        ) ;
        
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }
    
    private EquityHolding buildEquityHolding( String ownerName, String[] tupule ) {
        EquityHolding holding = new EquityHolding() ;
        
        holding.setOwnerName( ownerName ) ;
        holding.setSymbolIcici( tupule[0].trim() ) ;
        holding.setCompanyName( tupule[1].trim() ) ;
        holding.setIsin( tupule[2].trim() ) ;
        holding.setQuantity( Integer.parseInt( tupule[3].trim() ) ) ;
        holding.setAvgCostPrice( parseFloatAmt( tupule[4].trim() ) ) ;
        holding.setCurrentMktPrice( parseFloatAmt( tupule[5].trim() ) ) ;
        holding.setRealizedProfitLoss( parseFloatAmt( tupule[6].trim() ) ) ;
        holding.setLastUpdate( new Date() ) ;

        return holding ;
    }
}

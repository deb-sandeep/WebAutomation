package com.sandy.automator.icicidirect.newsite.equity;

import static com.sandy.automator.core.Util.parseFloatAmt ;

import java.io.File ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.automator.icicidirect.vo.equity.EquityTxnPosting ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

public class EquityTxnsCSVParser {
    
    static final Logger log = Logger.getLogger( EquityTxnsCSVParser.class ) ;

    private static final SimpleDateFormat SDF = new SimpleDateFormat( "dd-MMM-yyyy" ) ;
    
    public List<EquityTxnPosting> parseEquityTxns( String ownerName, 
                                                   File csvFile, 
                                                   List<String> equityFilter ) 
        throws Exception {

        CsvParser csvParser = getEquityTxnsCsvParser() ;

        List<EquityTxnPosting> txns = new ArrayList<>() ;
        List<String[]> csvFileContents = csvParser.parseAll( csvFile ) ;

        for( int i = 1 ; i < csvFileContents.size() ; i++ ) {
            String[] tupule = csvFileContents.get( i ) ;
            EquityTxnPosting txn = buildEquityTxn( ownerName, tupule ) ;
            
            if( equityFilter.isEmpty() ) {
                txns.add( txn ) ;
            }
            else if( equityFilter.contains( txn.getSymbolICICI() ) ) {
                txns.add( txn ) ;
            }
        }
        return txns ;
    }

    private CsvParser getEquityTxnsCsvParser() {

        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( 
                "Stock Symbol", 
                "Action", 
                "Quantity",
                "Transaction Price", 
                "Brokerage", 
                "Transaction Charges",
                "StampDuty", 
                "Transaction Date" 
        ) ;
        
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }

    private EquityTxnPosting buildEquityTxn( String ownerName, 
                                             String[] tupule )
        throws Exception {

        EquityTxnPosting txn = new EquityTxnPosting() ;

        txn.setOwnerName( ownerName ) ;
        txn.setSymbolICICI( tupule[0].trim() ) ;
        txn.setAction( tupule[1].trim() ) ;
        txn.setQuantity( Integer.parseInt( tupule[2].trim() ) ) ;
        txn.setTxnPrice( parseFloatAmt( tupule[3].trim() ) ) ;
        txn.setBrokerage( parseFloatAmt( tupule[4].trim() ) ) ;
        txn.setTxnCharges( parseFloatAmt( tupule[5].trim() ) ) ;
        txn.setStampDuty( parseFloatAmt( tupule[6].trim() ) ) ;
        txn.setTxnDate( SDF.parse( tupule[7].trim() ) ) ;

        return txn ;
    }
}

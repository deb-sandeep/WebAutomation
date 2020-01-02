package com.sandy.automation.browser.mfmetaenrich;

import java.io.File ;
import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

public class DownloadedFileProcessor {
    
    private static final Logger log = Logger.getLogger( DownloadedFileProcessor.class ) ;
    
    private String groupId = null ;
    private String companyName = null ;
    private File file = null ;
    
    DownloadedFileProcessor( String groupId, String coName, File file ) {
        
        this.groupId = groupId ;
        this.companyName = coName ;
        this.file = file ;
    }
    
    List<String[]> execute() throws Exception {
        
        log.debug( "\t\tParsing downloaded file.." ) ;
        CsvParser parser = getCsvParser() ;
        List<String[]> records = parser.parseAll( this.file ) ;
        
        for( Iterator<String[]> iter = records.iterator(); iter.hasNext(); ) {
            String[] record = iter.next() ;
            String isin = record[1] ;
            String structure = record[4] ;
            
            if( StringUtil.isEmptyOrNull( isin ) ) {
                iter.remove() ;
                continue ;
            }
            
            if( StringUtil.isEmptyOrNull( structure ) || 
                structure.trim().equals( "Closed Ended" ) ) {
                iter.remove() ;
                continue ;
            }
        }
        
        if( !records.isEmpty() ) {
            log.debug( "\t\t" + records.size() + " records found." ) ;
            List<String[]> postBody = preparePostData( records ) ;
            return postBody ;
        }
        return null ;
    }
    
    private CsvParser getCsvParser() {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        settings.selectFields( "FundName", "ISIN", "Category", 
                               "DistributionType", "Structure",
                               "LatestNAV", "NAVDate" ) ;
        
        CsvParser csvParser = new CsvParser( settings ) ;
        return csvParser ;
    }
    
    private List<String[]> preparePostData( List<String[]> records ) {
        List<String[]> postBody = new ArrayList<>() ;
        boolean isFirstRecord = true ;
        for( String[] record : records ) {
            if( isFirstRecord ) {
                // First record is the header.. continue.
                isFirstRecord = false ;
                continue ;
            }
            
            String[] postRecord = new String[8] ;
            postRecord[0] = this.groupId ;
            postRecord[1] = this.companyName ;
            postRecord[2] = record[1].trim() ; // ISIN
            postRecord[3] = record[0].trim() ; // Fund name
            postRecord[4] = record[2].trim() ; // Category
            postRecord[5] = record[3].trim() ; // Distribution type
            postRecord[6] = record[5] ; // Latest nav
            postRecord[7] = record[6] ; // NAV date
            postBody.add( postRecord ) ;
        }
        return postBody ;
    }
}

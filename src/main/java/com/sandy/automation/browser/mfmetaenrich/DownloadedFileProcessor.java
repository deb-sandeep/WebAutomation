package com.sandy.automation.browser.mfmetaenrich;

import java.io.File ;
import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.List ;
import java.util.concurrent.TimeUnit ;

import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.common.util.StringUtil ;
import com.univocity.parsers.csv.CsvParser ;
import com.univocity.parsers.csv.CsvParserSettings ;

import okhttp3.MediaType ;
import okhttp3.OkHttpClient ;
import okhttp3.Request ;
import okhttp3.RequestBody ;
import okhttp3.Response ;

public class DownloadedFileProcessor {
    
    private static final Logger log = Logger.getLogger( DownloadedFileProcessor.class ) ;
    public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" ) ; 
    
    private String groupId = null ;
    private String companyName = null ;
    private File file = null ;
    private String serverName = null ;
    
    DownloadedFileProcessor( String groupId, String coName, 
                             File file, String serverName ) {
        
        this.groupId = groupId ;
        this.companyName = coName ;
        this.file = file ;
        this.serverName = serverName ;
    }
    
    void execute() throws Exception {
        
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
            enrichMetaOnServer( postBody ) ;
        }
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
    
    private void enrichMetaOnServer( List<String[]> records )
        throws Exception {
        
        log.debug( "\t\tEnriching data on server." ) ;
        log.debug( "\t\tNumber of records = " + records.size() ) ;
        ObjectMapper objMapper = new ObjectMapper() ;
        String json = objMapper.writeValueAsString( records ) ;
        
        String url = "http://" + this.serverName + "/MutualFund/EnrichMFMeta" ;
        
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
        
        try {
            response = client.newCall( request ).execute() ;
            log.debug( "\t\tResponse from server - " + response.code() ) ;
            log.debug( "\t\t\t" + response.body().string() ) ;
        }
        finally {
            if( response != null ) {
                response.body().close() ;
            }
        }
    }

    public static void main( String[] args ) 
        throws Exception {
        
        String downloadDir = "/home/sandeep/projects/workspace/web_automation/downloads/" ;
        File csvFile = new File( downloadDir + "fundlist_adityabirla.csv" ) ;
        DownloadedFileProcessor processor = new DownloadedFileProcessor( 
                            "ADITYABIRLA", 
                            "Aditya Birla Sun Life AMC Ltd", 
                            csvFile, 
                            "localhost:8080" ) ;
        processor.execute() ;
    }
}

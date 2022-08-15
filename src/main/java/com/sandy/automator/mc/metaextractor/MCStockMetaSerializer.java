package com.sandy.automator.mc.metaextractor;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.io.ObjectInputStream ;
import java.io.ObjectOutputStream ;
import java.util.LinkedHashMap ;
import java.util.Map ;

import org.apache.log4j.Logger ;

public class MCStockMetaSerializer {
    
    private static final Logger log = Logger.getLogger( MCStockMetaSerializer.class ) ;

    public static final String PERSIST_FILE_KEY = "MCStockMeta" ;
    
    public void serializeObj( Map<String, MCStockMeta> map )
        throws Exception {
        
        File               file = getObjFile() ;
        FileOutputStream   fos  = new FileOutputStream( file ) ;
        ObjectOutputStream oos  = new ObjectOutputStream( fos ) ;
        
        oos.writeObject( map ) ;
        oos.close() ;
    }
    
    @SuppressWarnings( "unchecked" )
    public Map<String, MCStockMeta> deserializeObj() 
        throws Exception {
        
        File file = getObjFile( ) ;
        Map<String, MCStockMeta> map = null ;
        
        if( file.exists() ) {
            FileInputStream   fin = new FileInputStream( file ) ;
            ObjectInputStream ois = new ObjectInputStream( fin ) ;
            
            map = ( Map<String, MCStockMeta> )ois.readObject() ;
            ois.close() ;
        }
        else {
            map = new LinkedHashMap<>() ;
        }
        return map ;
    }
    
    private File getObjFile() {
        return new File( "/Users/sandeep/temp/" + PERSIST_FILE_KEY + ".obj" ) ;
    }
    
    public void serializeYAML( Map<String, MCStockMeta> map ) {
        
        StringBuilder sb = new StringBuilder( "stockCfgs:\n" ) ;
        for( MCStockMeta meta : map.values() ) {
            sb.append( "   - isin : " + meta.getIsin() + "\n" ) ; 
            sb.append( "     mcName : " + meta.getMcName() + "\n" ) ; 
            sb.append( "     symbolNSE : " + meta.getSymbolNSE() + "\n" ) ; 
            sb.append( "     detailURL : " + meta.getDetailURL() + "\n" ) ;
            sb.append( "\n" ) ;
        }
        log.debug( sb.toString() ) ;
    }
}

package com.sandy.automator.mc.stockisinmapper;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.io.ObjectInputStream ;
import java.io.ObjectOutputStream ;
import java.util.LinkedHashMap ;
import java.util.Map ;

public class Serializer {

    public void persist( Map<String, MCNameISIN> map, String objFileName )
        throws Exception {
        
        FileOutputStream fOut = new FileOutputStream( getFile( objFileName ) ) ;
        ObjectOutputStream oos = new ObjectOutputStream( fOut ) ;
        oos.writeObject( map ) ;
        oos.close() ;
    }
    
    @SuppressWarnings( "unchecked" )
    public Map<String, MCNameISIN> deserialize( String objFileName ) 
        throws Exception {
        
        File file = getFile( objFileName ) ;
        Map<String, MCNameISIN> map = null ;
        
        if( file.exists() ) {
            FileInputStream fIn = new FileInputStream( getFile( objFileName ) ) ;
            ObjectInputStream ois = new ObjectInputStream( fIn ) ;
            map = ( Map<String, MCNameISIN> )ois.readObject() ;
            ois.close() ;
        }
        else {
            map = new LinkedHashMap<>() ;
        }
        
        return map ;
    }
    
    private File getFile( String name ) {
        return new File( "/Users/sandeep/temp/" + name + ".obj" ) ;
    }
}

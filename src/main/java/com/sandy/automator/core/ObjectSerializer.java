package com.sandy.automator.core;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.io.ObjectInputStream ;
import java.io.ObjectOutputStream ;

import com.sandy.automator.Automator ;

public class ObjectSerializer {
    
    public void serializeObj( Object obj, String name )
        throws Exception {
        
        File               file = getObjFile( name ) ;
        FileOutputStream   fos  = new FileOutputStream( file ) ;
        ObjectOutputStream oos  = new ObjectOutputStream( fos ) ;
        
        oos.writeObject( obj ) ;
        oos.close() ;
    }
    
    public Object deserializeObj( String name ) 
        throws Exception {
        
        File file = getObjFile( name ) ;
        Object obj = null ;
        
        if( file.exists() ) {
            FileInputStream   fin = new FileInputStream( file ) ;
            ObjectInputStream ois = new ObjectInputStream( fin ) ;
            
            obj = ois.readObject() ;
            ois.close() ;
        }
        return obj ;
    }
    
    public void removeSerializedObject( String name ) {
        File file = getObjFile( name ) ;
        if( file.exists() ) {
            file.delete() ;
        }
    }
    
    private File getObjFile( String name ) {
        File serDir = new File( Automator.getWorkspacePath(), "ser" ) ;
        if( !serDir.exists() ) {
            serDir.mkdirs() ;
        }
        return new File( serDir, name + ".obj" ) ;
    }
}

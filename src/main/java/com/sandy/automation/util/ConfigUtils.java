package com.sandy.automation.util;

import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.Reader ;

import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler ;

public class ConfigUtils {

    public static PropertiesConfiguration loadPropertiesConfig( String basePropName ) 
            throws Exception {
        
        String resourcePath = "/" + basePropName + ".properties" ;
        
        InputStream is = ConfigUtils.class.getResourceAsStream( resourcePath ) ;
        Reader reader = new InputStreamReader( is ) ;
        
        PropertiesConfiguration config = new PropertiesConfiguration() ;
        config.setListDelimiterHandler( new DefaultListDelimiterHandler( ':' ) ) ;
        config.read( reader ) ;
        
        reader.close() ;
        return config ;
    }
}

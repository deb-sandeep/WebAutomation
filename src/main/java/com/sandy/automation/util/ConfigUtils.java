package com.sandy.automation.util;

import org.apache.commons.configuration2.Configuration ;
import org.apache.commons.configuration2.builder.fluent.Configurations ;

public class ConfigUtils {

    public static Configuration loadPropertiesConfig( String basePropName ) 
            throws Exception {
        
        Configurations configs = new Configurations() ;
        return configs.properties( 
                ConfigUtils.class
                           .getResource( "/" + basePropName + ".properties" )
                           .toURI()
                           .toURL() ) ;
    }
}

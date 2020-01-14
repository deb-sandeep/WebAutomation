package com.sandy.automator.core.cfg;

import java.util.HashMap ;
import java.util.Map ;

public class UseCaseAutomatorCfg {

    private String className = null ;
    private Map<String, String> configProperties = new HashMap<>() ;
    
    public String getClassName() {
        return className ;
    }
    public void setClassName( String className ) {
        this.className = className ;
    }
    
    public Map<String, String> getConfigProperties() {
        return configProperties ;
    }

    public void setConfigProperties( Map<String, String> props ) {
        this.configProperties = props ;
    }
    
    public Object stringify( String indent ) {
        StringBuilder builder = new StringBuilder( indent + "Use Case Automator Config :\n" ) ;
        builder.append( indent + "\tclassName = " + className + "\n" ) ; 
        builder.append( indent + "\tconfigProperties : \n" ) ;
        for( String key : configProperties.keySet() ) {
            builder.append( indent + "\t\t" + key + " = " + configProperties.get( key ) + "\n" ) ;
        }
        return builder.toString() ;
    }
}

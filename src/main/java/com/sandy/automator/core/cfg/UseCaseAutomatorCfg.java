package com.sandy.automator.core.cfg;

public class UseCaseAutomatorCfg extends BaseCfg {

    private boolean enabled = true ;
    private String ucId = null ;
    private String className = null ;
    
    public String getClassName() {
        return className ;
    }
    public void setClassName( String className ) {
        this.className = className ;
    }
    
    public boolean isEnabled() {
        return enabled ;
    }
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled ;
    }
    
    public String getUcId() {
        return ucId ;
    }
    public void setUcId( String ucId ) {
        this.ucId = ucId ;
    }
    
    public Object stringify( String indent ) {
        StringBuilder builder = new StringBuilder( indent + "Use Case Automator Config :\n" ) ;
        builder.append( indent + "\tucId = " + ucId + "\n" ) ; 
        builder.append( indent + "\tclassName = " + className + "\n" ) ; 
        builder.append( indent + "\tcenabled = " + enabled + "\n" ) ; 
        builder.append( indent + "\tconfigProperties : \n" ) ;
        for( String key : configProperties.keySet() ) {
            builder.append( indent + "\t\t" + key + " = " + configProperties.get( key ) + "\n" ) ;
        }
        return builder.toString() ;
    }
}

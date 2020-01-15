package com.sandy.automator.core.cfg;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

public class SiteAutomatorCfg {

    private String siteId = null ;
    private String className = null ;
    private List<UseCaseAutomatorCfg> useCaseAutomatorConfigs = new ArrayList<>() ;
    private List<SiteCredential> credentials = new ArrayList<>() ;
    private Map<String, String> configProperties = new HashMap<>() ;

    public String getSiteId() {
        return siteId ;
    }

    public void setSiteId( String siteId ) {
        this.siteId = siteId ;
    }

    public List<UseCaseAutomatorCfg> getUseCaseAutomatorConfigs() {
        return useCaseAutomatorConfigs ;
    }

    public void setUseCaseAutomatorConfigs( List<UseCaseAutomatorCfg> cfgs ) {
        this.useCaseAutomatorConfigs = cfgs ;
    }
    
    public List<SiteCredential> getCredentials() {
        return credentials ;
    }

    public void setCredentials( List<SiteCredential> credentials ) {
        this.credentials = credentials ;
    }

    public Map<String, String> getConfigProperties() {
        return configProperties ;
    }

    public void setConfigProperties( Map<String, String> props ) {
        this.configProperties = props ;
    }

    public String getClassName() {
        return className ;
    }
    public void setClassName( String className ) {
        this.className = className ;
    }
    
    public Object strinfigy( String indent ) {
        StringBuilder builder = new StringBuilder( indent + "Site Automator Config :\n" ) ;
        
        builder.append( indent + "\tsiteId = " + siteId + "\n" ) ;
        if( className != null ) {
            builder.append( indent + "\tclassName = " + className + "\n" ) ;
        }
        
        if( configProperties != null ) {
            builder.append( indent + "\tconfigProperties : \n" ) ;
            for( String key : configProperties.keySet() ) {
                builder.append( indent + "\t\t" + key + " = " + configProperties.get( key ) + "\n" ) ;
            }
        }
        
        builder.append( indent + "\tCredentials : \n" ) ;
        for( SiteCredential cred : credentials ) {
            builder.append( cred.stringify( indent + "\t\t" ) ) ;
        }
        
        for( UseCaseAutomatorCfg ucaCfg : useCaseAutomatorConfigs ) {
            builder.append( ucaCfg.stringify( indent + "\t" ) ) ;
        }
        return builder.toString() ;
    }
}

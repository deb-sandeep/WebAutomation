package com.sandy.automator.core.cfg;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

public class AutomatorCfg {

    private String workspacePath = null ;
    private boolean enableHeadless = false ;
    private boolean enableServerCommunication = true ;
    private Map<String, String> configProperties = new HashMap<>() ;
    
    private List<SiteAutomatorCfg> siteAutomatorConfigs = new ArrayList<>() ;
    
    public String getWorkspacePath() {
        return workspacePath ;
    }

    public void setWorkspacePath( String workspacePath ) {
        this.workspacePath = workspacePath ;
    }

    public boolean isEnableHeadless() {
        return enableHeadless ;
    }

    public void setEnableHeadless( boolean enableHeadless ) {
        this.enableHeadless = enableHeadless ;
    }

    public boolean isEnableServerCommunication() {
        return enableServerCommunication ;
    }

    public void setEnableServerCommunication( boolean enableServerCommunication ) {
        this.enableServerCommunication = enableServerCommunication ;
    }

    public List<SiteAutomatorCfg> getSiteAutomatorConfigs() {
        return siteAutomatorConfigs ;
    }

    public void setSiteAutomatorConfigs( List<SiteAutomatorCfg> cfgs ) {
        this.siteAutomatorConfigs = cfgs ;
    }
    
    public Map<String, String> getConfigProperties() {
        return configProperties ;
    }

    public void setConfigProperties( Map<String, String> props ) {
        this.configProperties = props ;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder( "Automator Config :\n" ) ;
        builder.append( "\tworkspacePath = " + workspacePath + "\n" )
               .append( "\tenableHeadless = " + enableHeadless + "\n" )
               .append( "\tenableServerCommunication = " + enableServerCommunication + "\n" )
               .append( "\tconfigProperties : \n" ) ;
        
        for( String key : configProperties.keySet() ) {
            builder.append( "\t\t" + key + " = " + configProperties.get( key ) + "\n" ) ;
        }

        for( SiteAutomatorCfg saCfg : siteAutomatorConfigs ) {
            builder.append( saCfg.strinfigy( "\t" ) ) ;
        }
        return builder.toString() ;
    }
}

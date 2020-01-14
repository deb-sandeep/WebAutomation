package com.sandy.automator.core.builder;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;

import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory ;
import com.sandy.automator.core.cfg.AutomatorCfg ;
import com.sandy.automator.core.cfg.SiteAutomatorCfg ;
import com.sandy.common.util.StringUtil ;

public class ConfigBuilder {

    private static final Logger log = Logger.getLogger( ConfigBuilder.class ) ;
    
    private ObjectMapper mapper = null ; 
    
    public AutomatorCfg loadConfig() throws Exception {
        
        mapper = new ObjectMapper( new YAMLFactory() ) ; 
        mapper.findAndRegisterModules() ;
        
        InputStream is = getClass().getResourceAsStream( "/automator-config.yaml" ) ;
        AutomatorCfg config = mapper.readValue( is, AutomatorCfg.class ) ;

        for( SiteAutomatorCfg saCfg : config.getSiteAutomatorConfigs() ) {
            loadSiteCredentials( saCfg ) ;
        }
        
        return config ;
    }

    private void loadSiteCredentials( SiteAutomatorCfg config ) 
        throws Exception {
        
        String siteId = config.getSiteId() ;
        if( StringUtil.isEmptyOrNull( siteId ) ) {
            throw new RuntimeException( "Found a site configuration without a siteId" ) ;
        }
        
        String siteCredFileName = siteId + "-creds.yaml" ;
        File credsCfgFile = new File( System.getProperty( "user.home" ), 
                                      siteCredFileName ) ;
        
        if( !credsCfgFile.exists() ) {
            log.info( "Credentials for site " + siteId + " not found." ) ;
            return ;
        }
        
        InputStream credsIs = new FileInputStream( credsCfgFile ) ;
        SiteAutomatorCfg saCfg = mapper.readValue( credsIs, SiteAutomatorCfg.class ) ;
        config.getCredentials().addAll( saCfg.getCredentials() ) ;
    }
}

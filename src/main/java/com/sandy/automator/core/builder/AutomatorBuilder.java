package com.sandy.automator.core.builder;

import java.io.File ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Map ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.commons.beanutils.PropertyUtils ;
import org.apache.commons.configuration2.PropertiesConfiguration ;
import org.apache.log4j.Logger ;

import com.sandy.automator.Automator ;
import com.sandy.automator.core.Configurable ;
import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.UseCaseAutomator ;
import com.sandy.automator.core.cfg.AutomatorCfg ;
import com.sandy.automator.core.cfg.SiteAutomatorCfg ;
import com.sandy.automator.core.cfg.UseCaseAutomatorCfg ;
import com.sandy.common.util.StringUtil ;

public class AutomatorBuilder {

    private static final Logger log = Logger.getLogger( AutomatorBuilder.class ) ;
    
    public void buildAutomator( Automator automator ) 
        throws Exception {
        
        SiteAutomator siteAutomator = null ;
        ConfigBuilder cfgBuilder = new ConfigBuilder() ;
        AutomatorCfg automatorCfg = cfgBuilder.loadConfig() ;
        
        log.debug( automatorCfg ) ;
        
        configureAutomator( automatorCfg, automator ) ;
        
        for( SiteAutomatorCfg saCfg : automatorCfg.getSiteAutomatorConfigs() ) {
            if( saCfg.isEnabled() ) {
                siteAutomator = buildSiteAutomator( 
                                        saCfg, 
                                        automatorCfg.getConfigProperties(),
                                        automator ) ;
                automator.addSiteAutomator( siteAutomator ) ;
            }
            else {
                log.info( "Site automator " + saCfg.getSiteId() + " is not enabled." );
            }
        }
    }
    
    private void configureAutomator( AutomatorCfg cfg, Automator automator ) 
        throws Exception {
        
        String cfgVal = cfg.getWorkspacePath() ;
        if( cfgVal == null ) {
            String msg = "Invalid workspacePath config. Path not configured." ;
            throw new IllegalArgumentException( msg ) ;
        }
        else {
            File workspacePath = new File( cfgVal ) ;
            if( !workspacePath.exists() ) {
                String msg = "Invalid workspacePath config. Path doesn't exist." ;
                throw new IllegalArgumentException( msg ) ;
            }
            else {
                automator.setWorkspacePath( workspacePath ) ;
            }
        }
        automator.setEnableHeadless( cfg.isEnableHeadless() ) ;
        automator.setEnableServerCommunication( cfg.isEnableServerCommunication() ) ;
    }
    
    @SuppressWarnings( "unchecked" )
    private SiteAutomator buildSiteAutomator( SiteAutomatorCfg saCfg,
                                              Map<String, String> automatorCfgProps,
                                              Automator automator ) 
        throws Exception {
        
        UseCaseAutomator ucAutomator = null ;
        SiteAutomator siteAutomator = null ;
        
        String className = saCfg.getClassName() ;
        if( StringUtil.isEmptyOrNull( className ) ) {
            siteAutomator = new SiteAutomator() ;
        }
        else {
            Class<? extends SiteAutomator> clazz = null ;
            clazz = ( Class<? extends SiteAutomator> ) Class.forName( className ) ;
            siteAutomator = clazz.getDeclaredConstructor().newInstance() ;
        }
        
        siteAutomator.setParentAutomator( automator ) ;
        siteAutomator.setSiteId( saCfg.getSiteId() ) ;
        
        Map<String, String> scProps = new HashMap<>() ;
        if( automatorCfgProps != null ) {
            scProps.putAll( automatorCfgProps ) ;
        }
        if( saCfg.getConfigProperties() != null ) {
            scProps.putAll( saCfg.getConfigProperties() ) ;
        }
        
        injectProperties( siteAutomator, scProps ) ;
        
        for( UseCaseAutomatorCfg ucCfg : saCfg.getUseCaseAutomatorConfigs() ) {
            if( ucCfg.isEnabled() ) {
                ucAutomator = buildUseCaseAutomator( ucCfg, scProps, siteAutomator ) ;
                siteAutomator.addUseCaseAutomator( ucAutomator ) ;
            }
            else {
                log.info( "Usecase automator " + ucCfg.getClassName() + " is not enabled." ) ;
            }
        }
        return siteAutomator ;
    }
    
    @SuppressWarnings( "unchecked" )
    private UseCaseAutomator buildUseCaseAutomator( UseCaseAutomatorCfg ucCfg,
                                                    Map<String, String> saProps,
                                                    SiteAutomator siteAutomator ) 
        throws Exception {
        
        UseCaseAutomator ucAutomator = null ;
        
        String className = ucCfg.getClassName() ;
        if( StringUtil.isEmptyOrNull( className ) ) {
            String msg = "Invalid use case automator config. className doesn't exist." ;
            throw new IllegalArgumentException( msg ) ;
        }
        
        if( StringUtil.isEmptyOrNull( ucCfg.getUcId() ) ) {
            throw new IllegalArgumentException( "Use case id not specified." ) ;
        }
        
        Class<? extends UseCaseAutomator> clazz = null ;
        clazz = ( Class<? extends UseCaseAutomator> )Class.forName( className ) ;
        ucAutomator = clazz.getDeclaredConstructor().newInstance() ;
        ucAutomator.setSiteAutomator( siteAutomator ) ;
        ucAutomator.setUcId( ucCfg.getUcId() ) ;
        
        Map<String, String> ucProps = new HashMap<>() ;
        ucProps.putAll( saProps ) ;
        ucProps.putAll( ucCfg.getConfigProperties() ) ;
        
        injectProperties( ucAutomator, ucProps ) ;
        
        return ucAutomator ;
    }

    private void injectProperties( Configurable configurable, 
                                   Map<String, String> props ) 
        throws Exception {
        
        PropertiesConfiguration propsCfg = new PropertiesConfiguration() ;
        for( String key : props.keySet() ) {
            String value = props.get( key ) ;
            propsCfg.addProperty( key, value ) ;
        }
        
        propsCfg = ( PropertiesConfiguration )propsCfg.interpolatedConfiguration() ;
        for( Iterator<String> iter = propsCfg.getKeys(); iter.hasNext(); ) {
            String key = iter.next() ;
            Object value = propsCfg.getProperty( key ) ;
            if( PropertyUtils.isWriteable( configurable, key ) ) {
                BeanUtils.setProperty( configurable, key, value ) ;
            }
        }
        configurable.setPropertiesConfiguation( propsCfg ) ;
    }
}

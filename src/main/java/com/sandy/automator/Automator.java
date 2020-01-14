package com.sandy.automator;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.automator.core.SiteAutomator ;
import com.sandy.automator.core.builder.ConfigBuilder ;
import com.sandy.automator.core.cfg.AutomatorCfg ;

public class Automator {
    
    private static final Logger log = Logger.getLogger( Automator.class ) ;
    
    private List<SiteAutomator> siteAutomators = new ArrayList<>() ;

    public void initialize() throws Exception {
        log.debug( "Initializing Automator" ) ;
        AutomatorCfg cfg = new ConfigBuilder().loadConfig() ;
        // TODO: Build automators - note, building also involves initialization
        // TODO: Execute the automators. Think of abstracting and sharing web driver
        log.debug( cfg ) ;
    }
    
    public void execute() throws Exception {
        log.debug( "Executing automator." ) ;
    }
    
    public static void main( String[] args ) {
        try {
            Automator automator = new Automator() ;
            automator.initialize() ;
            automator.execute() ;
        }
        catch( Exception e ) {
            log.error( "Automator exited due to an exception.", e ) ;
        }
    }
}

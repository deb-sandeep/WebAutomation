package com.sandy.automator.core;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.configuration2.PropertiesConfiguration ;

import com.sandy.automator.Automator ;

public class SiteAutomator implements Configurable {

    private Automator parentAutomator = null ;
    private List<UseCaseAutomator> useCaseAutomators = new ArrayList<>() ;
    protected PropertiesConfiguration config = null ;
    
    public void setParentAutomator( Automator automator ) {
        this.parentAutomator = automator ;
    }
    
    public Automator getParentAutomator() {
        return this.parentAutomator ;
    }
    
    public void addUseCaseAutomator( UseCaseAutomator automator ) {
        this.useCaseAutomators.add( automator ) ;
    }

    @Override
    public void setPropertiesConfiguation( PropertiesConfiguration config ) {
        this.config = config ;
    }
}

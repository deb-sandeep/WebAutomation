package com.sandy.automator.core;

import org.apache.commons.configuration2.PropertiesConfiguration ;

public abstract class UseCaseAutomator implements Configurable {

    private SiteAutomator parentAutomator = null ;
    protected PropertiesConfiguration config = null ;
    
    public SiteAutomator getParentAutomator() {
        return parentAutomator ;
    }

    public void setParentAutomator( SiteAutomator automator ) {
        this.parentAutomator = automator ;
    }
    
    @Override
    public void setPropertiesConfiguation( PropertiesConfiguration config ) {
        this.config = config ;
    }
}

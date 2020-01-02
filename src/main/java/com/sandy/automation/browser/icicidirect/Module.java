package com.sandy.automation.browser.icicidirect;

import java.util.List ;

import org.apache.commons.configuration2.CombinedConfiguration ;

public abstract class Module {
    
    protected ICICIDirectAutomation browser = null ;
    protected List<Cred> credentials = null ;
    protected CombinedConfiguration config = null ;

    public Module() {}
    
    public void setParent( ICICIDirectAutomation parent ) {
        this.browser = parent ;
    }
    
    public void setCredentials( List<Cred> credentials ) {
        this.credentials = credentials ;
    }
    
    public void setConfiguration( CombinedConfiguration config ) {
        this.config = config ;
    }
    
    public abstract void execute() throws Exception ;
}

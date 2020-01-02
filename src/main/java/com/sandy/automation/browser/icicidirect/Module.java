package com.sandy.automation.browser.icicidirect;

import java.util.List ;

public abstract class Module {
    
    protected ICICIDirectAutomation parent = null ;
    protected List<Cred> credentials = null ;

    public Module() {}
    
    public void setParent( ICICIDirectAutomation parent ) {
        this.parent = parent ;
    }
    
    public void setCredentials( List<Cred> credentials ) {
        this.credentials = credentials ;
    }
    
    public abstract void execute() throws Exception ;
}

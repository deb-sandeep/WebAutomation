package com.sandy.automator.core;

import org.apache.commons.configuration2.PropertiesConfiguration ;

import com.sandy.automator.core.cfg.SiteCredential ;

public abstract class UseCaseAutomator implements Configurable {

    private SiteAutomator siteAutomator = null ;
    
    protected String ucId = null ;
    protected PropertiesConfiguration config = null ;
    
    public SiteAutomator getSiteAutomator() {
        return siteAutomator ;
    }
    public void setSiteAutomator( SiteAutomator parent ) {
        this.siteAutomator = parent ;
    }
    
    public String getUcId() {
        return ucId ;
    }
    public void setUcId( String ucId ) {
        this.ucId = ucId ;
    }
    
    @Override
    public void setPropertiesConfiguation( PropertiesConfiguration config ) {
        this.config = config ;
    }
    
    /*
     * If the site credential is not null, it is assumed that the 
     * credential has been logged into the site and will be logged out
     * sometimes after this automator finishes execution. Essentially this
     * automator should not worry about login/logout of the credential.
     */
    public abstract void execute( SiteCredential cred, Browser browser ) 
            throws Exception ;
}

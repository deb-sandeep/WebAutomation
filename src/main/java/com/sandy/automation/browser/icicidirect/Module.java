package com.sandy.automation.browser.icicidirect;

public class Module {

    private String cls = null ;
    private boolean enabled = true ;
    private String description = null ;
    
    public Module( String cls, boolean enabled, String description ) {
        this.cls = cls ;
        this.enabled = enabled ;
        this.description = description ;
    }
    
    public String getCls() {
        return cls ;
    }
    
    public boolean isEnabled() {
        return enabled ;
    }
    
    public String getDescription() {
        return description ;
    }
    
    @Override
    public String toString() {
        return "Module [" + 
            "\ncls=" + cls + 
            "\nenabled=" + enabled + 
            "\ndescription=" + description + 
            "\n]" ;
    }
}

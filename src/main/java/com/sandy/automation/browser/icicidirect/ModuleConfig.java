package com.sandy.automation.browser.icicidirect;

public class ModuleConfig {

    private String id = null ;
    private String cls = null ;
    private boolean enabled = true ;
    private String description = null ;
    
    public ModuleConfig( String id, String cls, 
                         boolean enabled, String description ) {
        this.id = id ;
        this.cls = cls ;
        this.enabled = enabled ;
        this.description = description ;
    }
    
    public String getId() {
        return id ;
    }
    
    public String getClassName() {
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

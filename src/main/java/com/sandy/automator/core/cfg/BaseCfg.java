package com.sandy.automator.core.cfg;

import java.util.HashMap ;
import java.util.Map ;

public abstract class BaseCfg {

    protected Map<String, String> configProperties = new HashMap<>() ;

    public Map<String, String> getConfigProperties() {
        return configProperties ;
    }

    public void setConfigProperties( Map<String, String> props ) {
        this.configProperties = props ;
    }
}

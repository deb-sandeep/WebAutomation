package com.sandy.automator.icicidirect;

import com.sandy.automator.core.UseCaseAutomator ;

public class MFPortfolioUseCaseAutomator extends UseCaseAutomator {

    private String property = null ;

    public String getProperty() {
        return property ;
    }

    public void setProperty( String property ) {
        System.out.println( "Setting property = " + property ) ;
        this.property = property ;
    }
}

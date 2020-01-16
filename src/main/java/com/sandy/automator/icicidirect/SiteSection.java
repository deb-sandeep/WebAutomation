package com.sandy.automator.icicidirect;

import org.openqa.selenium.By ;

public enum SiteSection {
    
    // TI - Trade & Invest section
    // TI_PS - Portfolio & Statements sub-section of TI section
    // TI_PS_MF - Mutual Fund portfolio & statements
    
    TI( SelectorType.LINK_TEXT, "Trade & Invest",
        "Trade & Invest main section" ),
    
    TI_PS( SelectorType.ID, "hypPF",
        "Portfolio & Statements sub section of Trade & Invest section"),
    
    TI_PS_MF ( SelectorType.CSS, "label[title='Mutual Funds']",
        "Mutual Funds portfolio & statements") ;

    enum SelectorType {
        CSS, ID, LINK_TEXT
    } ;
    
    private SelectorType selType = null ;
    private String value = null ;
    private String description = null ;
    
    private SiteSection( SelectorType selType, String value, String description ) {
        this.selType = selType ;
        this.value = value ;
        this.description = description ;
    }

    public By getSelector() {
        switch( selType ) {
            case LINK_TEXT:
                return By.linkText( value ) ;
            case ID:
                return By.id( value ) ;
            case CSS:
                return By.cssSelector( value ) ;
        }
        return null ;
    }
    
    public String getDescription() {
        return this.description ;
    }
}

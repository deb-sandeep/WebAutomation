package com.sandy.automator.icicidirect.newsite;

import org.openqa.selenium.By ;

public enum NewSiteSection {
    
    // TI - Trade & Invest section
    // TI_PS - Portfolio & Statements sub-section of TI section
    // TI_PS_MF - Mutual Fund portfolio & statements
    
    SECTION_PORTFOLIO( SelectorType.LINK_TEXT, "Portfolio",
                       "Portfolio main section" ),
    
    SUBSECTION_EQUITY( SelectorType.XPATH, "//*[@id=\"pnlmnudsp\"]/div[1]/div/ul/li[1]/a",
                       "Equity subsection within portfolio section" ),
    
    TI( SelectorType.LINK_TEXT, "Trade & Invest",
        "Trade & Invest main section" ),
    
    TI_PS( SelectorType.ID, "hypPF",
        "Portfolio & Statements sub section of Trade & Invest section"),
    
    TI_PS_MF ( SelectorType.CSS, "label[title='Mutual Funds']",
        "Mutual Funds portfolio & statements"),
    
    TI_PS_EQ ( SelectorType.CSS, "label[title='Equity']",
            "Equity portfolio & statements") ;

    enum SelectorType {
        CSS, ID, LINK_TEXT, XPATH
    } ;
    
    private SelectorType selType = null ;
    private String value = null ;
    private String description = null ;
    
    private NewSiteSection( SelectorType selType, String value, String description ) {
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
            case XPATH:
                return By.xpath( value ) ;
        }
        return null ;
    }
    
    public String getDescription() {
        return this.description ;
    }
}

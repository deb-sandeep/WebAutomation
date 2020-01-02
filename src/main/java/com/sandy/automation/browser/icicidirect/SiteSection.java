package com.sandy.automation.browser.icicidirect;

public enum SiteSection {
    
    TRADE_INVEST( "Trade & Invest" ),
    RESEARCH( "Research" ) ;

    private String linkText = null ;
    
    private SiteSection( String linkText ) {
        this.linkText = linkText ;
    }
    
    public String getLinkText() {
        return this.linkText ;
    }
}

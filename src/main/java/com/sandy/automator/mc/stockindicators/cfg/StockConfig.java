package com.sandy.automator.mc.stockindicators.cfg;

import lombok.Data ;

@Data
public class StockConfig {

    private String isin = null ;
    private String mcName = null ;
    private String symbolNSE = null ;
    private String detailURL = null ;
}

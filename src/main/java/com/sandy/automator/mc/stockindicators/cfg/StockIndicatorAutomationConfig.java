package com.sandy.automator.mc.stockindicators.cfg;

import java.util.ArrayList ;
import java.util.List ;

import lombok.Data ;

@Data
public class StockIndicatorAutomationConfig {

    private boolean runFresh = false ;
    private List<StockConfig> stockCfgs = new ArrayList<>() ;
}

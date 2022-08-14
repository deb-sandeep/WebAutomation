package com.sandy.automator.mc.stockcharacteristics.cfg;

import java.util.ArrayList ;
import java.util.List ;

import lombok.Data ;

@Data
public class StockAttributesUpdateConfig {

    private List<StockConfig> stockCfgs = new ArrayList<>() ;
}

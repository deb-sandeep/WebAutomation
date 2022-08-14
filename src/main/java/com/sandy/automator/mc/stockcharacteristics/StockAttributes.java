package com.sandy.automator.mc.stockcharacteristics;

import java.util.ArrayList ;
import java.util.List ;

import lombok.Data ;

@Data
public class StockAttributes {
    
    @Data
    public static class TechIndicator {
        private String name = null ;
        private float level = 0 ;
        private String indication = null ;
    }

    private String isin = null ;
    private String symbolNSE = null ;
    
    private float beta = 0 ;
    private float high52 = 0 ;
    private float low52 = 0 ;
    private float eps = 0 ;
    private float pe = 0 ;
    private float sectorPE = 0 ;
    private float pb = 0 ;
    
    private String trend = null ;
    
    private float[] movingAverages = null ; // 5, 10, 20, 50, 100, 200
    private int[] communitySentiments = null ; // Buy, Sell, Hold
    
    private List<TechIndicator> indicators = new ArrayList<>() ;
}

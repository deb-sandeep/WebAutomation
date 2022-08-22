package com.sandy.automator.mc.stockindicators;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import lombok.Data ;

//
// IMPORTANT NOTE
//
// Remember that an identical copy is stored at 
// /CapitalystServer/../api/equity/vo/StockIndicators.java. This copy needs to
// be structurally similar to this copy. So if there are any changes on this
// end it, needs to be reflected on the other end too.
//
@Data
public class StockIndicators {
    
    @Data
    public static class TechIndicator {
        private String name = null ;
        private float level = 0 ;
        private String indication = null ;
    }

    private String isin           = null ;
    private String symbolNse      = null ;
    private String sector         = null ;
    private float  beta           = 0 ;
    private float  high52         = 0 ;
    private float  low52          = 0 ;
    private float  eps            = 0 ;
    private float  pe             = 0 ;
    private float  sectorPE       = 0 ;
    private float  pb             = 0 ;
    private float  dividendYeild  = 0 ;
    private float  cagrRevenue    = 0 ;
    private float  cagrNetProfit  = 0 ;
    private float  cagrEbit       = 0 ;
    private int    marketCap      = 0 ;
    private int    piotroskiScore = 0 ;
    private Date   asOnDate       = null ;
    
    private float currentPrice = 0 ;
    
    private String trend = null ;
    private int    mcEssentialScore = 0 ;
    private String mcInsightShort = null ;
    private String mcInsightLong = null ;
    
    private float sma5   = 0 ;
    private float sma10  = 0 ;
    private float sma20  = 0 ;
    private float sma50  = 0 ;
    private float sma100 = 0 ;
    private float sma200 = 0 ;
    
    private int communitySentimentBuy  = 0 ;
    private int communitySentimentSell = 0 ;
    private int communitySentimentHold = 0 ;
    
    private float pricePerf1W  = 0 ;
    private float pricePerf1M  = 0 ;
    private float pricePerf3M  = 0 ;
    private float pricePerf1Y  = 0 ;
    private float pricePerf3Y  = 0 ;
    private float pricePerfYTD = 0 ;
    
    private List<TechIndicator> indicators = new ArrayList<>() ;
}

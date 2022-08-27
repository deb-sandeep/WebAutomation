package com.sandy.automator.mc.metaextractor;

import java.io.Serializable ;

import lombok.Data ;

@Data
public class MCStockMeta implements Serializable {

    private static final long serialVersionUID = 2244901068311057725L ;
    
    private String isin = null ;
    private String symbolNSE = null ;
    private String mcName = null ;
    private String detailURL = null ;
    private String description = null ;
}

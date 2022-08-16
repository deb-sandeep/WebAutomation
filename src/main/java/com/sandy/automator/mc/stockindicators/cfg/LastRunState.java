package com.sandy.automator.mc.stockindicators.cfg;

import java.io.Serializable ;
import java.util.Date ;
import java.util.Set ;
import java.util.TreeSet ;

import lombok.Data ;

@Data
public class LastRunState implements Serializable {

    private static final long serialVersionUID = 768476924752832049L ;
    
    private Date saveDate = new Date() ;
    private Set<String> processedISINList = new TreeSet<>() ;
}

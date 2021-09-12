package com.sandy.automator.icicidirect.vo.equity;

import java.util.Date ;

public class EquityHolding {

    private String ownerName = null ;
    private String symbolIcici = null ;
    private String companyName = null ;
    private String isin = null ;
    private int quantity = 0 ;
    private float avgCostPrice = 0.0f ;
    private float currentMktPrice = 0.0f ;
    private float valueAtCost = 0.0f ;
    private float realizedProfitLoss = 0.0f ;
    private Date lastUpdate = null ;
    
    public EquityHolding() {}
    
    public void setOwnerName( String val ) {
        this.ownerName = val ;
    }
        
    public String getOwnerName() {
        return this.ownerName ;
    }

    public void setSymbolIcici( String val ) {
        this.symbolIcici = val ;
    }
        
    public String getSymbolIcici() {
        return this.symbolIcici ;
    }

    public void setCompanyName( String val ) {
        this.companyName = val ;
    }
        
    public String getCompanyName() {
        return this.companyName ;
    }

    public void setIsin( String val ) {
        this.isin = val ;
    }
        
    public String getIsin() {
        return this.isin ;
    }

    public void setQuantity( int val ) {
        this.quantity = val ;
    }
        
    public int getQuantity() {
        return this.quantity ;
    }

    public void setAvgCostPrice( float val ) {
        this.avgCostPrice = val ;
    }
        
    public float getAvgCostPrice() {
        return this.avgCostPrice ;
    }

    public void setCurrentMktPrice( float val ) {
        this.currentMktPrice = val ;
    }
        
    public float getCurrentMktPrice() {
        return this.currentMktPrice ;
    }

    public void setValueAtCost( float val ) {
        this.valueAtCost = val ;
    }
        
    public float getValueAtCost() {
        return this.valueAtCost ;
    }

    public void setRealizedProfitLoss( float val ) {
        this.realizedProfitLoss = val ;
    }
        
    public float getRealizedProfitLoss() {
        return this.realizedProfitLoss ;
    }

    public Date getLastUpdate() {
        return lastUpdate ;
    }

    public void setLastUpdate( Date lastUpdate ) {
        this.lastUpdate = lastUpdate ;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder( "EquityHolding [\n" ) ; 
        
        builder.append( "   ownerName = " + this.ownerName + "\n" ) ;
        builder.append( "   symbolIcici = " + this.symbolIcici + "\n" ) ;
        builder.append( "   companyName = " + this.companyName + "\n" ) ;
        builder.append( "   isin = " + this.isin + "\n" ) ;
        builder.append( "   quantity = " + this.quantity + "\n" ) ;
        builder.append( "   avgCostPrice = " + this.avgCostPrice + "\n" ) ;
        builder.append( "   currentMktPrice = " + this.currentMktPrice + "\n" ) ;
        builder.append( "   valueAtCost = " + this.valueAtCost + "\n" ) ;
        builder.append( "   realizedProfitLoss = " + this.realizedProfitLoss + "\n" ) ;
        builder.append( "   lastUpdate = " + this.lastUpdate + "\n" ) ;
        builder.append( "]" ) ;
        
        return builder.toString() ;
    }
}
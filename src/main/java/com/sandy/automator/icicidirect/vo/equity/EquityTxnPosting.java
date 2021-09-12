package com.sandy.automator.icicidirect.vo.equity;

import java.util.Date ;

public class EquityTxnPosting {

    private String ownerName = null ;
    private String symbolICICI = null ;
    private String action = null ;
    private int quantity = 0 ;
    private Date txnDate = null ;
    private float txnPrice = 0.0f ;
    private float brokerage = 0.0f ;
    private float txnCharges = 0.0f ;
    private float stampDuty = 0.0f ;
    
    public EquityTxnPosting() {}
    
    public String getOwnerName() {
        return ownerName ;
    }

    public void setOwnerName( String ownerName ) {
        this.ownerName = ownerName ;
    }
    
    public String getSymbolICICI() {
        return symbolICICI ;
    }

    public void setSymbolICICI( String symbolICICI ) {
        this.symbolICICI = symbolICICI ;
    }

    public void setAction( String val ) {
        this.action = val ;
    }
        
    public String getAction() {
        return this.action ;
    }

    public void setQuantity( int val ) {
        this.quantity = val ;
    }
        
    public int getQuantity() {
        return this.quantity ;
    }

    public void setTxnPrice( float val ) {
        this.txnPrice = val ;
    }
        
    public float getTxnPrice() {
        return this.txnPrice ;
    }

    public void setBrokerage( float val ) {
        this.brokerage = val ;
    }
        
    public float getBrokerage() {
        return this.brokerage ;
    }

    public void setTxnCharges( float val ) {
        this.txnCharges = val ;
    }
        
    public float getTxnCharges() {
        return this.txnCharges ;
    }

    public void setStampDuty( float val ) {
        this.stampDuty = val ;
    }
        
    public float getStampDuty() {
        return this.stampDuty ;
    }

    public void setTxnDate( Date val ) {
        this.txnDate = val ;
    }
        
    public Date getTxnDate() {
        return this.txnDate ;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder( "EquityTxn [\n" ) ; 
        
        builder.append( "   ownerName = " + this.ownerName + "\n" ) ;
        builder.append( "   symbol = " + this.symbolICICI + "\n" ) ;
        builder.append( "   action = " + this.action + "\n" ) ;
        builder.append( "   quantity = " + this.quantity + "\n" ) ;
        builder.append( "   txnPrice = " + this.txnPrice + "\n" ) ;
        builder.append( "   brokerage = " + this.brokerage + "\n" ) ;
        builder.append( "   txnCharges = " + this.txnCharges + "\n" ) ;
        builder.append( "   stampDuty = " + this.stampDuty + "\n" ) ;
        builder.append( "   txnDate = " + this.txnDate + "\n" ) ;
        builder.append( "]" ) ;
        
        return builder.toString() ;
    }
}
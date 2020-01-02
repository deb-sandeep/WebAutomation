package com.sandy.automation.browser.icicidirect.mfportfolio ;

import java.text.SimpleDateFormat ;
import java.util.Date ;

public class MFTxn {
    
    public static SimpleDateFormat SDF = new SimpleDateFormat( "dd-MM-yyyy" ) ;

    private String ownerName = null ;
    private String scheme = null ;
    private String txnType = null ;
    private String txnChannel = null ;
    private Date txnDate = null ;
    private float navPerUnit = 0.0f ;
    private float numUnits = 0.0f ;
    private float amount = 0.0f ;

    public MFTxn() {}
    
    public void setOwnerName( String val ) {
        this.ownerName = val ;
    }
        
    public String getOwnerName() {
        return this.ownerName ;
    }

    public void setScheme( String val ) {
        this.scheme = val ;
    }
        
    public String getScheme() {
        return this.scheme ;
    }

    public void setTxnType( String val ) {
        this.txnType = val ;
    }
        
    public String getTxnType() {
        return this.txnType ;
    }

    public void setTxnChannel( String val ) {
        this.txnChannel = val ;
    }
        
    public String getTxnChannel() {
        return this.txnChannel ;
    }

    public void setTxnDate( Date val ) {
        this.txnDate = val ;
    }
        
    public Date getTxnDate() {
        return this.txnDate ;
    }

    public void setNavPerUnit( float val ) {
        this.navPerUnit = val ;
    }
        
    public float getNavPerUnit() {
        return this.navPerUnit ;
    }

    public void setNumUnits( float val ) {
        this.numUnits = val ;
    }
        
    public float getNumUnits() {
        return this.numUnits ;
    }

    public void setAmount( float val ) {
        this.amount = val ;
    }
        
    public float getAmount() {
        return this.amount ;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder( "MutualFundTxn [\n" ) ; 
        
        builder.append( "   ownerName = " + this.ownerName + "\n" ) ;
        builder.append( "   scheme = " + this.scheme + "\n" ) ;
        builder.append( "   txnType = " + this.txnType + "\n" ) ;
        builder.append( "   txnChannel = " + this.txnChannel + "\n" ) ;
        builder.append( "   txnDate = " + this.txnDate + "\n" ) ;
        builder.append( "   navPerUnit = " + this.navPerUnit + "\n" ) ;
        builder.append( "   numUnits = " + this.numUnits + "\n" ) ;
        builder.append( "   amount = " + this.amount + "\n" ) ;
        builder.append( "]" ) ;
        
        return builder.toString() ;
    }
}
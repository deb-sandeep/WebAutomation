package com.sandy.automator.icicibank.cc;

import java.text.DecimalFormat ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

import org.apache.commons.lang3.StringUtils ;

public class CCTxnEntry {
    
    public static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;
    public static final DecimalFormat DF = new DecimalFormat( "#.00" ) ;

    private String creditCardNumber = null ;
    private Date valueDate = null ;
    private String remarks = null ;
    private float amount = 0 ;
    private float balance = 0 ;
    
    public String getCreditCardNumber() {
        return creditCardNumber ;
    }

    public void setCreditCardNumber( String ccNo ) {
        this.creditCardNumber = ccNo ;
    }

    public Date getValueDate() {
        return valueDate ;
    }

    public void setValueDate( Date valueDate ) {
        this.valueDate = valueDate ;
    }

    public String getRemarks() {
        return remarks == null ? "" : remarks ;
    }

    public void setRemarks( String remarks ) {
        this.remarks = remarks ;
    }

    public float getAmount() {
        return amount ;
    }

    public void setAmount( float amount ) {
        this.amount = amount ;
    }

    public float getBalance() {
        return balance ;
    }

    public void setBalance( float balance ) {
        this.balance = balance ;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder() ;
        builder.append( StringUtils.rightPad( creditCardNumber, 20 ) ) ;
        builder.append( StringUtils.rightPad( SDF.format( valueDate ), 12 ) ) ;
        builder.append( StringUtils.rightPad( remarks, 45 ) ) ;
        builder.append( StringUtils.leftPad( DF.format( amount ), 15 ) ) ;
        builder.append( StringUtils.leftPad( DF.format( balance ), 15 ) ) ;
        return builder.toString() ;
    }
}

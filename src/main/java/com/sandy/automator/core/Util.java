package com.sandy.automator.core;

public class Util {


    public static float parseFloatAmt( String input ) {
        if( input.equals( "NA" ) ) {
            return 0 ;
        }
        
        if( input.startsWith( "(" ) ) {
            input = input.substring( 1, input.length()-1 ) ;
            return -1*Float.parseFloat( input ) ;
        }
        return Float.parseFloat( input ) ;
    }
}

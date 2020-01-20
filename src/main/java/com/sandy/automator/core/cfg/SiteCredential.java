package com.sandy.automator.core.cfg;

import java.util.HashMap ;
import java.util.Map ;

public class SiteCredential {

    private String individualName = null ;
    private String userName = null ;
    private String password = null ;
    private Map<String, String> extraAttributes = new HashMap<>() ;
    
    public String getIndividualName() {
        return individualName ;
    }
    public void setIndividualName( String individualName ) {
        this.individualName = individualName ;
    }
    
    public String getUserName() {
        return userName ;
    }
    public void setUserName( String userName ) {
        this.userName = userName ;
    }
    
    public String getPassword() {
        return password ;
    }
    public void setPassword( String password ) {
        this.password = password ;
    }
    
    public Map<String, String> getExtraAttributes() {
        return extraAttributes ;
    }
    public void setExtraAttributes( Map<String, String> extraAttributes ) {
        this.extraAttributes = extraAttributes ;
    }
    
    public String getAttribute( String attributeName ) {
        return extraAttributes.get( attributeName ) ;
    }
    
    public boolean getBooleanAttribute( String attributeName ) {
        String attrVal = getAttribute( attributeName ) ;
        return Boolean.parseBoolean( attrVal ) ;
    }
    
    public Object stringify( String indent ) {
        StringBuilder builder = new StringBuilder( indent + "Credential :\n" ) ;
        builder.append( indent + "\tindividualName = " + individualName + "\n" ) ;
        builder.append( indent + "\tuserName = " + userName + "\n" ) ;
        builder.append( indent + "\tpassword = " + password + "\n" ) ;
        
        for( String key : extraAttributes.keySet() ) {
            builder.append( indent + "\t" + key + " = " + extraAttributes.get( key ) + "\n" ) ;
        }
        return builder.toString() ;
    }
}

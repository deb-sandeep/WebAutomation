package com.sandy.automation.browser.icicidirect;

public class Cred {

    private String userName = null ;
    private String individualName = null ;
    private String password = null ;
    private String dob = null ;
    
    public Cred( String userName, String individualName, 
                 String password, String dob ) {
        this.userName = userName ;
        this.individualName = individualName ;
        this.password = password ;
        this.dob = dob ;
    }

    public String getUserName() {
        return this.userName ;
    }
    
    public String getPassword() {
        return password ;
    }
    
    public String getDob() {
        return dob ;
    }
    
    public String getIndividualName() {
        return individualName ;
    }
    
    public String toString() {
        return "Cred [userName=" + userName + 
                ", password=" + password + 
                ", dob=" + dob + 
                ". individual=" + individualName + "]" ;
    }
}

package com.sandy.automation.browser.icicidirect;

public class Cred {

    private String userName = null ;
    private String password = null ;
    private String dob = null ;
    
    public Cred( String userName, String password, String dob ) {
        this.userName = userName ;
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
    
    @Override
    public String toString() {
        return "Cred [userName=" + userName + 
                ", password=" + password + 
                ", dob=" + dob + "]" ;
    }
}

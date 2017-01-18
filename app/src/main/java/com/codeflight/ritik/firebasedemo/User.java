package com.codeflight.ritik.firebasedemo;

/**
 * Created by ritik on 1/18/2017.
 */

public class User {
    private String userName;
    private String email;
    private String mobile;
    private String dob;

    public User()
    {

    }

    public User(String username,String email,String mobile,String dob)
    {
        this.userName = username;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        mobile = mobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        dob = dob;
    }
}

// This file is for the UserLogin class, which defines the structure of a UserLogin object
//   as well as the methods which may be used on a UserLogin Object.
//     The UserLogin object holds all pertinent data that is associated with a single registered user

package com.zybooks.cs360finalproject_aleksbevz;

public class UserLogin {

    private String mUsername;
    private String mPassword;
    private String mPhoneNumber;
    private String mReceiveSMS;  //Enhancement 2 - added to store user's choice for receiving SMS notifications
    private long mAlertEditedDW; //Enhancement 2 - added to flag edited daily weights (used to check if an edited DW equals the goal weight)
    private String mUserRole;    //Enhancement 3, Part 1 - added due to addition of role based access

    public UserLogin() {}

    //constructor for UserLogin object
    public UserLogin(String username, String password, String phoneNumber, String receiveSMS, long alertEditedDW, String userRole) {
        mUsername = username;
        mPassword = password;
        mPhoneNumber = phoneNumber;
        mReceiveSMS = receiveSMS;
        mAlertEditedDW = alertEditedDW;
        mUserRole = userRole;
    }

    // various methods used to set or get any of the parameters associated with a single UserLogin object
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getReceiveSMS() {
        return mReceiveSMS;
    }

    public void setReceiveSMS(String receiveSMS) {
        mReceiveSMS = receiveSMS;
    }

    public long getAlertEditedDW() {
        return mAlertEditedDW;
    }

    public void setAlertEditedDW(long alertEditedDW) {
        mAlertEditedDW = alertEditedDW;
    }

    public String getUserRole() {
        return mUserRole;
    }

    public void setUserRole(String userRole) {
        mUserRole = userRole;
    }
}

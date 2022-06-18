// This file defines the smsPermissionActivity, which is launched when a user taps the messsage icon
//   in the app bar. In this activity, a user can opt in to receive SMS notifications when their goal weight
//     is reached. If the user clicks yes, they are given an option to enter their phone number.


package com.zybooks.cs360finalproject_aleksbevz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class smsPermissionActivity extends AppCompatActivity {

    // Widgets
    private EditText mEditPhone;
    private Button mSaveButton;

    // vars and objects
    private UserLogin mUserLogin;
    private String mUsername;
    private WeightTrackerDatabase mWeightTrackerDB;

    // extras
    public static final String EXTRA_USERNAME = "com.zybooks.weighttracker.username";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        //Assign widgets to fields
        mSaveButton = findViewById(R.id.buttonSave);
        mEditPhone = findViewById(R.id.phoneEditText);

        // Get username extra from launching activity so sms/phone info will be associated with current user
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(EXTRA_USERNAME);

        // get instance of db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        // get user login object from db
        mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);

    }


    public void onYesClick(View view){

        // display widgets for user to enter and save phone number
        mEditPhone.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);

        // Check to see if permission has been granted, else request permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //based on permission status, make a toast to show user if SMS notifications are enabled or not
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "SMS notifications enabled.",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS notifications not enabled.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }


    // If user does not enable SMS notifications, finish activity (no further action needed)
    public void onNoClick(View view){
        finish();
    }

    // onclick method to save user's entered phone number
    public void onSaveClick(View view) {

        //get phone number from edit text
        String phoneNumber = mEditPhone.getText().toString();

        //update phone number and SMS permissions for user login object
        mUserLogin.setPhoneNumber(phoneNumber);
        mUserLogin.setReceiveSMS("yes");

        //update user in db
        mWeightTrackerDB.updateUser(mUserLogin);

        finish();
    }
}
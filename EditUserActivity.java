// Enhancement 3, Part 2: role-based access
// This Activity is only accessible from the AdminActivity (which is only accessible to admins)
// This Activity allows admins to delete or edit the roles of other users

package com.zybooks.cs360finalproject_aleksbevz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditUserActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "com.zybooks.weighttracker.username";

    //widgets
    private TextView mUsernameTextView;
    private Button mBasicButton;
    private Button mAdminButton;
    private Button mSaveButton;

    //vars and objects
    private WeightTrackerDatabase mWeightTrackerDB;
    private UserLogin mUserLogin;
    private String mUsername;
    private String mUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //assign widgets to fields
        mUsernameTextView = findViewById(R.id.usernameTextView);
        mBasicButton = findViewById(R.id.basicButton);
        mAdminButton = findViewById(R.id.adminButton);
        mSaveButton = findViewById(R.id.saveButton);

        //get instance of db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        // Get username from calling activity so edit action is associated with correct user
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(EXTRA_USERNAME);

        //set text to username to show which user is being edited
        mUsernameTextView.setText(mUsername);

        //get user object corresponding to username from db
        mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);

    }

    // executes when basic button is clicked
    public void onBasicClick(View view) {
        //change button enablement to show selection
        mBasicButton.setEnabled(false);  //disabled = selected
        mAdminButton.setEnabled(true);   //enabled = not selected

        //set role string
        mUserRole = "basicUser";

        //display save button
        mSaveButton.setVisibility(View.VISIBLE);

    }

    // executes when admin button is clicked
    public void onAdminClick(View view) {
        //change button enablement to show selection
        mBasicButton.setEnabled(true);   //enabled = not selected
        mAdminButton.setEnabled(false);  //disabled = selected

        //set role string
        mUserRole = "adminUser";

        //display save button
        mSaveButton.setVisibility(View.VISIBLE);

    }

    // executes when save button is clicked
    public void onSaveClick(View view) {
        // update user role for user and save to database
        mUserLogin.setUserRole(mUserRole);
        mWeightTrackerDB.updateUser(mUserLogin);

        finish();
    }

}
// This file defines the LoginRegisterActivity, which is the first activity shown upon app launch.
//   This activity is where registered users can log into the application. Unregistered users are also
//      able to register here.

package com.zybooks.cs360finalproject_aleksbevz;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginRegisterActivity extends AppCompatActivity {

    private EditText mUsernameText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private Button mRegisterButton;
    private UserLogin mAdmin;

    private WeightTrackerDatabase mWeightTrackerDB;

    // TextWatcher for dynamically enabling/disabling the Login and Register buttons
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //enable button when user enters text
            if (s.length() > 0) {
                mLoginButton.setEnabled(true);
                mRegisterButton.setEnabled(true);
            }
            // when there is no text, disable button
            else {
                mLoginButton.setEnabled(false);
                mRegisterButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Assign widgets to fields
        mUsernameText = findViewById(R.id.usernameText);
        mPasswordText = findViewById(R.id.passwordText);
        mLoginButton = findViewById(R.id.loginButton);
        mRegisterButton = findViewById(R.id.registerButton);

        //get instance of weight tracker db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        // Set text changed listener for the password EditText field
        mPasswordText.addTextChangedListener(textWatcher);

        // Search database for default/main admin user
        mAdmin = mWeightTrackerDB.getUserByUsername("admin");

        // if default/main admin does not already exist, create a new one
        if (mAdmin == null){
            createAdminUser();
        }
    }

    // Enhancement 3, Part 1 - role-based access
    // to create a default admin if one does not already exist (i.e., upon creation of new version of database)
    public void createAdminUser(){
        String username = "admin";
        String password = "securePassword123";
        String phoneNumber = "15555215554";   //admin can change this, but setting to my emulator's number for now
        String receiveSMS = "yes";
        long alertEditedDW = -1;              //default is that no daily weights have been edited
        String userRole = "adminUser";

        //create new user object with above values as arguments
        mAdmin = new UserLogin(username, password, phoneNumber, receiveSMS, alertEditedDW, userRole);

        mWeightTrackerDB.addUser(mAdmin);
    }

    // onClick method for the Login button - authenticates entered credentials
    public void loginClick(View view){
        //get username and password from edit text fields
        String userName = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        // check for successful authentication (ensures username/password combo was found in db)
        boolean userFound = mWeightTrackerDB.getUserByCredentials(userName, password);

        // if authentication was successful:
        if (userFound == true) {
            // Launch next activity
            Intent intent = new Intent(this, WeightDisplayActivity.class);
            intent.putExtra(WeightDisplayActivity.EXTRA_USERNAME, userName);
            startActivity(intent);
        }
        // if authentication was not successful:
        else {
            // Display error message
            // Enhancement 1 (UI updates) - replaced TextView message with a dialog error message
            FragmentManager manager = getSupportFragmentManager();
            InvalidLoginDialogFragment dialog = new InvalidLoginDialogFragment();
            dialog.show(manager, "invalidLogin");

            // Clear EditText fields so user can try again
            mUsernameText.setText("");
            mPasswordText.setText("");
        }


    }


    // onClick method for the Register button - adds new user to user database
    public void registerClick(View view){
        //get username and password from edit text fields
        String userName = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        String phoneNumber = "";   //set phone number to empty string; will be added later if user chooses
        String receiveSMS = "no";  //default for allowing SMS notifications
        long alertEditedDW = -1;   //default is that no daily weights have been edited
        String userRole = "basicUser"; //default is that new users are basic users

        //create new user object with above values as arguments
        UserLogin userLogin = new UserLogin(userName, password, phoneNumber, receiveSMS, alertEditedDW, userRole);


        // check for successful user add
        boolean successfulUserAdd = mWeightTrackerDB.addUser(userLogin);

        // If user is added, display dialog fragment that prompts login
        // Enhancement 1 (UI updates) - replaced TextView message with a dialog
        if (successfulUserAdd == true) {
            FragmentManager manager = getSupportFragmentManager();
            UserCreatedDialogFragment dialog = new UserCreatedDialogFragment();
            dialog.show(manager, "userCreated");
        }
        // If user cannot be added (ie due to duplicate username), display error message
        // Enhancement 1 (UI updates) - replaced TextView message with a dialog
        else {
            FragmentManager manager = getSupportFragmentManager();
            UnsuccessfulRegistrationDialogFragment dialog = new UnsuccessfulRegistrationDialogFragment();
            dialog.show(manager, "unsuccessfulRegistration");
        }

        // Clear EditText fields so user can try again
        mUsernameText.setText("");
        mPasswordText.setText("");

    }
}
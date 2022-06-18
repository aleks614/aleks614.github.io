// This file defines the WeightDisplayActivity, which is the first screen that a user sees after a
//   successful login. This activity allows users to view or delete their goal weight and daily weights. The user
//     can also launch other activities that allow them to add/edit their goal weight and daily weights


package com.zybooks.cs360finalproject_aleksbevz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class WeightDisplayActivity extends AppCompatActivity {

    // Widgets
    private TextView mGoalWeightTextView;

    // Variables and objects and extras
    private Menu mMenu;
    private WeightTrackerDatabase mWeightTrackerDB;
    private GoalWeight mGoalWeight = new GoalWeight();
    private String mUsername;
    private UserLogin mUserLogin;
    private long mAlertEditedDW;
    private String mPhoneNo;
    private String mSMSMessage;
    public static final String EXTRA_USERNAME = "com.zybooks.weighttracker.username";
    private List<DailyWeight> mDailyWeights = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_display);

        // Assign widgets to fields
        mGoalWeightTextView = findViewById(R.id.goalWeightTextView);

        // Hosting activity provides the username for the logged in user
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(EXTRA_USERNAME);

        //get instance of db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        //get user object from db
        mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);

        // user's goal weight and daily weights are constantly displayed
        displayGoalWeight();
        displayDailyWeight();

    }

    // creates menu in app bar for sms and admin icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    // Enhancement 3, Part 2 - role-based access
    // Admin users have an admin icon in app bar
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        UserLogin userLogin;  // local user object because a user's role can be edited, so want to get the most up to date user object
        String userRole;
        MenuItem adminMenuItem;

        //assign admin icon widget to field
        adminMenuItem = mMenu.findItem(R.id.admin_menu_item);

        //get user object from db and check role
        userLogin = mWeightTrackerDB.getUserByUsername(mUsername);
        userRole = userLogin.getUserRole();

        //admin icon is only visible to those with admin role
        if (userRole.equals("adminUser")){
            adminMenuItem.setVisible(true);
        } else {
            adminMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            //if SMS icon is clicked, launch sms Permission Activity
            case R.id.action_notifications:
                intent = new Intent(WeightDisplayActivity.this, smsPermissionActivity.class);
                //put username as extra so the SMS info will be saved for current user
                intent.putExtra(smsPermissionActivity.EXTRA_USERNAME, mUsername);
                startActivity(intent);
                return true;

                //if admin icon is clicked, launch Admin Activity
            case R.id.admin_menu_item:
                intent = new Intent(WeightDisplayActivity.this, AdminActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Enhancement 2, Part 1 - replaced startActivityForResult and onActivityResult with ActivityResultLauncher
    // This will be used to launch the DailyWeightActivity rather than the SMSPermissionActivity
    ActivityResultLauncher<Intent>  weightDisplayActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //this is only executed when DailyWeightActivity ends, but only if DailyWeightActivity
                // was launched by this Launcher (i.e. if a new daily weight was added)
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        //need to get user object again to get up to date value of receiveSMS
                        mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);
                        String receiveSMS = mUserLogin.getReceiveSMS();

                        //if user has allowed SMS notifications, call method to check if goal weight is reached
                        if (receiveSMS.equals("yes")) {
                            compareGoalWeight();

                        }
                    }
                }
            }
    );


    @Override
    protected void onResume(){
        super.onResume();

        // user's goal weight and daily weights are constantly displayed
        displayGoalWeight();
        displayDailyWeight();

        //Enhancement 2 - added check to see if the user has edited a daily weight and has allowed SMS notifications
        //if yes, then compareGoalWeight() will execute
        mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);
        mAlertEditedDW = mUserLogin.getAlertEditedDW();
        String receiveSMS = mUserLogin.getReceiveSMS();

        if ((mAlertEditedDW != -1) && (receiveSMS.equals("yes"))){  //indicates a daily weight has been edited
            compareGoalWeight();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        // user's goal weight and daily weights are constantly displayed
        displayGoalWeight();
        displayDailyWeight();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    private List<DailyWeight> loadDailyWeights() {
        return mWeightTrackerDB.getDailyWeights(mUsername);
    }


    // Check for goal weight reached
    // Enhancement 2 - removed loop which checked for goal weight reached;
    // instead, only the last added or edited daily weight is checked
    public void compareGoalWeight() {
        String date;
        String weight;
        List<DailyWeight> dailyWeightList;
        String goalWeightValue;
        DailyWeight lastDailyWeight;

        //get goal weight object for user from db
        mGoalWeight = mWeightTrackerDB.getGoalWeight(mUsername);

        //get goal weight value from goal weight object
        goalWeightValue = mGoalWeight.getGoalWeightValue();

        //if a daily weight has been edited, check to see if it is equal to goal weight
        if (mAlertEditedDW != -1) {
            //get edited DW object from db
            DailyWeight editedDailyWeight = mWeightTrackerDB.getDailyWeight(mAlertEditedDW);

            //get weight and date from edited DW object
            weight = editedDailyWeight.getDailyWeight();
            date = editedDailyWeight.getDate();

            //send SMS if goal weight is reached (daily weight = goal weight)
            if (weight.equals(goalWeightValue)){
                sendSMS(date, goalWeightValue);
            }
            //update user object's alertEditedDW flag back to -1 so user does not continue to receive SMS for same DW
            mUserLogin.setAlertEditedDW(-1);

            //update user in db after resetting flag
            mWeightTrackerDB.updateUser(mUserLogin);
        }
        // if no daily weights have been edited, check last entered daily weight
        else {
            //get daily weights list and ensure it has data
            dailyWeightList = loadDailyWeights();
            if (dailyWeightList.size() != 0) {

                //get last daily weight so only that one is checked (previously looped through all daily weights)
                lastDailyWeight = dailyWeightList.get(dailyWeightList.size()-1);

                //get weight and date of last DW
                weight = lastDailyWeight.getDailyWeight();
                date = lastDailyWeight.getDate();

                //send SMS if goal weight is reached
                if (weight.equals(goalWeightValue)){
                    sendSMS(date, goalWeightValue);
                }
            }
        }

    }

    // to send SMS when goal weight is reached
    // changed second parameter to String (was previously GoalWeight)
    public void sendSMS(String date, String goalWeightValue) {
        //get phone number for user
        mPhoneNo = mUserLogin.getPhoneNumber();

        //SMS message will contain date the goal weight was reached
        mSMSMessage = "Congrats! On " + date + ", you reached your goal weight of " +
                goalWeightValue + "!";

        //set up sms manager to send the text
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mPhoneNo, null, mSMSMessage, null, null);

    }


    // onClick method to add OR edit goal weight
    public void editGoalWeightClick(View view) {
        //launch GoalWeightActivity and pass username as an extra
        Intent intent = new Intent(this, GoalWeightActivity.class);
        intent.putExtra(GoalWeightActivity.EXTRA_USERNAME, mUsername);
        startActivity(intent);
    }

    // onClick method to delete goal weight
    public void deleteGoalWeightClick(View view) {
        //delete from db
        mWeightTrackerDB.deleteGoalWeight(mUsername);

        //update goal weight display
        displayGoalWeight();
    }


    // method to display the user's goal weight
    public void displayGoalWeight() {
        String goalText;

        //get goal weight for user from db
        mGoalWeight = mWeightTrackerDB.getGoalWeight(mUsername);

        //if user already has a goal weight, set goalText to the goal weight value
        if (mGoalWeight != null) {
            goalText = mGoalWeight.getGoalWeightValue();
        }
        //if user does not have a goal weight, set goalText to an empty string
        else {
            goalText = "";
        }

        //display goalText string in the goal weight display (textview)
        mGoalWeightTextView.setText(goalText);
    }


    // onClick method to add new daily weight data
    public void addNewDailyWeightClick(View view) {
        //launch DailyWeightActivity
        Intent intent = new Intent(this, DailyWeightActivity.class);
        intent.putExtra(DailyWeightActivity.EXTRA_USERNAME, mUsername);            //put username as extra so DWs are associated with current user
        intent.putExtra(DailyWeightActivity.EXTRA_LAUNCHING_ACTION, "add");  //also put Extra to tell next activity that user wants to add a new DW
        weightDisplayActivityResultLauncher.launch(intent);
    }

    // To display the recycler view of daily weight data
    private void displayDailyWeight() {
        mRecyclerView = findViewById(R.id.recyclerView);

        // Shows the available daily weights
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, loadDailyWeights());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}
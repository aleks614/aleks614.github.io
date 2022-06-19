// This file defines the DailyWeightActivity, which is where users add or edit daily weight entries.
//   The action that launches this activity could be either the "Add new daily weight" button click
//   from WeightDisplayActivity, or it could be the edit button click from a single daily weight entry in the recycler view.


package com.zybooks.cs360finalproject_aleksbevz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyWeightActivity extends AppCompatActivity {

    // Widgets
    private EditText mDateInput;
    private EditText mWeightInput;
    private Button mSaveButton;
    private DatePickerDialog mDatePickerDialog;
    private TextView mTitleText;

    // Vars and objects
    public static final String EXTRA_USERNAME = "com.zybooks.weighttracker.username";
    public static final String EXTRA_DAILY_WEIGHT_ID = "com.zybooks.weighttracker.dailyWeightId";
    public static final String EXTRA_LAUNCHING_ACTION = "com.zybooks.weighttracker.launchingAction";
    private String mUsername;
    private long mId;
    private WeightTrackerDatabase mWeightTrackerDB;
    private DailyWeight mDailyWeight;
    private List<DailyWeight> mDailyWeightList = new ArrayList<>();
    private String mLaunchingAction;

    // TextWatcher for dynamically enabling/disabling the save button
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //enable button when user enters text
            if (s.length() > 0) {
                mSaveButton.setEnabled(true);
            }
            // Disable button if no text
            else {
                mSaveButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_weight);

        //assign widgets to id's in the layout
        mDateInput = findViewById(R.id.dateEditText);
        mWeightInput = findViewById(R.id.weightEditText);
        mSaveButton = findViewById(R.id.saveButton);
        mTitleText = findViewById(R.id.addEditDataTitle);

        // get instance of db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        // Get the intent from launching activity
        Intent intent = getIntent();

        // Get the string extra from launching activity to tell us what the user is trying to do ("add" or "edit")
        mLaunchingAction = intent.getStringExtra(EXTRA_LAUNCHING_ACTION);

        // if user is adding a new DW
        if (mLaunchingAction.equals("add")) {
            // Get the username from WeightDisplayActivity, so new daily weight is associated with current user
            mUsername = intent.getStringExtra(EXTRA_USERNAME);
        }
        // if user is editing an existing DW
        else if (mLaunchingAction.equals("edit")) {
            // Get the id of the DW entry so the correct one is displayed and updated
            mId = intent.getLongExtra(EXTRA_DAILY_WEIGHT_ID, -1);

            //change activity title to show that we are editing data
            mTitleText.setText(R.string.edit_data);

            // get daily weight object corresponding to the given id (new method created for this purpose)
            mDailyWeight = mWeightTrackerDB.getDailyWeight(mId);

            // get current values to display in EditTexts
            String currentDate = mDailyWeight.getDate();
            String currentWeight = mDailyWeight.getDailyWeight();

            // put current values in EditTexts
            mDateInput.setText(currentDate);
            mWeightInput.setText(currentWeight);

            mSaveButton.setEnabled(true); //enable button since fields will already have entries

        }


        // Set text changed listener for the EditText
        // still need this if user is editing a DW as they may clear the edit text
        mWeightInput.addTextChangedListener(textWatcher);

        // Enhancement 1 (UI update) - make date input clickable but disable typing
        // as a date picker will be used instead (also a security enhancement)
        mDateInput.setFocusable(false);
        mDateInput.setClickable(true);

        // date picker
        mDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set up calendar for date picker
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                mDatePickerDialog = new DatePickerDialog(DailyWeightActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mDateInput.setText((month + 1) + "/" + dayOfMonth + "/" + year);  //set date text to the selected date in Month/Day/Year format
                    }
                }, year, month, day);

                mDatePickerDialog.show();
            }
        });
    }


    // Saves the new/edited daily weight
    public void onSaveClick(View view) {
        //add new daily weight if user is adding data
        if (mLaunchingAction.equals("add")){
            // get data from edit text fields
            String date = mDateInput.getText().toString();
            String dailyWeightVal = mWeightInput.getText().toString();

            // Create new daily weight obhect and add to db
            mDailyWeight = new DailyWeight(date, dailyWeightVal, mUsername);
            mWeightTrackerDB.addDailyWeight(mDailyWeight);
        }
        //update existing daily weight if user is editing data
        else if (mLaunchingAction.equals("edit")){
            // get data from edit text fields
            String editedDate = mDateInput.getText().toString();
            String editedWeight = mWeightInput.getText().toString();

            // Enhancement 1, Part 1: update daily weight object with new date and weight
            mDailyWeight.setDate(editedDate);
            mDailyWeight.setDailyWeight(editedWeight);
            // Update in database
            mWeightTrackerDB.updateDailyWeight(mDailyWeight);
        }

        // Enhancement 2 - added two lines below to send result code back to calling activity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }
}
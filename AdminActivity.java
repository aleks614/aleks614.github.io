// Enhancement 3, Part 2 - as part of role-based access, admin users can access this activity to delete or update users

package com.zybooks.cs360finalproject_aleksbevz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private WeightTrackerDatabase mWeightTrackerDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //get instance of weight tracker db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        //user list is constantly displayed
        displayUserList();
    }

    @Override
    protected void onResume(){
        super.onResume();

        //user list is constantly displayed
        displayUserList();
    }

    @Override
    protected void onStart(){
        super.onStart();

        //user list is constantly displayed
        displayUserList();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    //method returns a list of user objects
    private List<UserLogin> loadUsers() {
        return mWeightTrackerDB.getUserList();
    }

    //displays user info in a recycler view
    private void displayUserList() {
        //uses the adminRecyclerView
        RecyclerView adminRecyclerView;
        com.zybooks.cs360finalproject_aleksbevz.AdminRecyclerViewAdapter adminRecyclerViewAdapter;

        adminRecyclerView = findViewById(R.id.recyclerViewAdmin);

        //set up the admin recycler view adapter with the list of user objects
        adminRecyclerViewAdapter = new AdminRecyclerViewAdapter(this, loadUsers());
        adminRecyclerView.setAdapter(adminRecyclerViewAdapter);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
// This file defines the RecyclerViewAdapter which is responsible for generating the recycler view list
//   of the logged in user's daily weight entries. This recycler view is shown in WeightDisplayActivity.
//     Each daily weight entry has an edit button, a date TextView, a weight TextView, and a delete button.

package com.zybooks.cs360finalproject_aleksbevz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<DailyWeight> mDailyWeightList;
    private Context mContext;
    private WeightTrackerDatabase mWeightTrackerDB;
    private UserLogin mUserLogin;


    // Default constructor
    public RecyclerViewAdapter(Context context, List<DailyWeight> dailyWeightList) {
        mDailyWeightList = dailyWeightList;
        mContext = context;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        //create a view holder with the row_daily_weight layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_daily_weight, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerViewAdapter.ViewHolder holder, int position) {
        //each daily weight object from the list will be bound to a position in the view holder
        holder.bind(mDailyWeightList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDailyWeightList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mDateText;
        private TextView mWeightText;
        private ImageButton mEditButton;
        private ImageButton mDeleteButton;

        // variables
        private DailyWeight mDailyWeight;
        private String mDate;
        private String mWeight;
        private long mId;
        private String mUsername;
        private DailyWeight mEditedDailyWeight;


        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            // For Enhancement 1 recycler view updates, added the below line (missing before)
            // get instance of db
            mWeightTrackerDB = WeightTrackerDatabase.getInstance(mContext.getApplicationContext());

            // Assign widgets and layouts to id's
            mDateText = itemView.findViewById(R.id.dateTextView);
            mWeightText = itemView.findViewById(R.id.weightTextView);
            mEditButton = itemView.findViewById(R.id.editImageButton);
            mDeleteButton = itemView.findViewById(R.id.deleteImageButton);
        }

        public void bind(DailyWeight dailyWeight, int position) {
            //get daily weight info for daily weight object at a given position in list
            mDailyWeight = dailyWeight;
            mDate = mDailyWeight.getDate();
            mWeight = mDailyWeight.getDailyWeight();
            mId = mDailyWeight.getId();
            mUsername = mDailyWeight.getUsername();

            //set text fields to date and weight
            mDateText.setText(mDate);
            mWeightText.setText(mWeight);

            // set tags to id for daily weight so the buttons are associated with the correct daily weight
            mEditButton.setTag(mId);
            mDeleteButton.setTag(mId);


            // OnClick method to edit individual daily weight entries
            //    Enhancement 1, Part 1: fixed this (previously display and DB would not update with
            //    updated info; updated daily weight in list used to generate recycler view, and also
            //    edited save method in DailyWeightActivity.java to properly update the daily weight object in DB)
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // intent to launch edit activity
                    Intent intent = new Intent(mContext, DailyWeightActivity.class);
                    intent.putExtra(DailyWeightActivity.EXTRA_DAILY_WEIGHT_ID, mId);    //changed extra from username to id so correct daily weight is edited
                    intent.putExtra(DailyWeightActivity.EXTRA_LAUNCHING_ACTION, "edit");  //also put Extra to tell next activity that user wants to edit DW
                    mContext.startActivity(intent);


                    // use getDailyWeight method (created for this purpose) to get edited daily
                    // weight after DailyWeightActivity ends
                    mEditedDailyWeight = mWeightTrackerDB.getDailyWeight(mId);

                    int index = mDailyWeightList.indexOf(mDailyWeight);
                    if (index >= 0) {
                        //update list with edited daily weight so that recycler view updates
                        mDailyWeightList.set(index, mEditedDailyWeight);

                        //notify adapter so that recycler view updates
                        notifyItemChanged(position);

                    }

                    // once a daily weight is edited, get user login object from db and
                    // update the user's alertEditedDW flag
                    mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);
                    mUserLogin.setAlertEditedDW(mId);    //set to id of edited DW so WeightDisplayActivity can compare to goal weight
                    mWeightTrackerDB.updateUser(mUserLogin);

                }
            });


            // OnClick method to delete individual daily weight entries
            //    Enhancement 1, Part 2: fixed this (previously app would crash when delete button was clicked and
            //    daily weight would not be deleted; now daily weights are deleted not only from DB
            //    but also from list used to generate recycler view)

            mDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // delete DW from database
                    mWeightTrackerDB.deleteDailyWeight(mId);

                    int index = mDailyWeightList.indexOf(mDailyWeight);

                    if (index >= 0) {
                        // remove daily weight from list to ensure recycler view updates
                        mDailyWeightList.remove(index);

                        //notify adapter to ensure recycler view updates
                        notifyItemRemoved(position);
                    }

                }
            });

        }


}
}

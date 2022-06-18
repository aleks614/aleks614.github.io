// Enhancement 3, Part 2: role-based access
// This file defines the AdminRecyclerViewAdapter which is responsible for generating the recycler view list
//   of the current active users. This recycler view is shown in AdminActivity.
//     Each user has an edit button, a username TextView, a user role TextView, and a delete button.

package com.zybooks.cs360finalproject_aleksbevz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;



public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.ViewHolder> {

    private List<UserLogin> mUserList;
    private Context mContext;
    private WeightTrackerDatabase mWeightTrackerDB;

    // Default constructor
    public AdminRecyclerViewAdapter(Context context, List<UserLogin> userList) {
        mUserList = userList;
        mContext = context;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        //create a view holder using the row_user layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull AdminRecyclerViewAdapter.ViewHolder holder, int position) {
        //each user object from the user list will be bound to a position in the view holder
        holder.bind(mUserList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mUsernameText;
        private TextView mRoleText;
        private ImageButton mEditButton;
        private ImageButton mDeleteButton;


        // variables
        private UserLogin mUserLogin;
        private String mUsername;
        private String mRole;
        private UserLogin mEditedUserLogin;


        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            //get instance of weight tracker db
            mWeightTrackerDB = WeightTrackerDatabase.getInstance(mContext.getApplicationContext());

            // Assign widgets and layouts to id's
            mUsernameText = itemView.findViewById(R.id.usernameTextView);
            mRoleText = itemView.findViewById(R.id.roleTextView);
            mEditButton = itemView.findViewById(R.id.editImageButton);
            mDeleteButton = itemView.findViewById(R.id.deleteImageButton);

        }

        public void bind(UserLogin userLogin, int position) {
            // get user info for UserLogin object at a given position in list
            mUserLogin = userLogin;
            mUsername = mUserLogin.getUsername();
            mRole = mUserLogin.getUserRole();

            // display username and role for each user
            mUsernameText.setText(mUsername);
            mRoleText.setText(mRole);

            // set tags to username so each button corresponds to the right user
            mEditButton.setTag(mUsername);
            mDeleteButton.setTag(mUsername);

            //edit and delete buttons are invisible for main admin (prevents unwanted edit/delete of this user)
            if (mUsername.equals("admin")){
                mEditButton.setVisibility(View.INVISIBLE);
                mDeleteButton.setVisibility(View.INVISIBLE);
            }


            // OnClick method to edit individual users
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // intent to launch edit activity with username as an extra
                    Intent intent = new Intent(mContext, EditUserActivity.class);
                    intent.putExtra(EditUserActivity.EXTRA_USERNAME, mUsername);
                    mContext.startActivity(intent);


                    // get edited user object from database once Edit Activity ends
                    mEditedUserLogin = mWeightTrackerDB.getUserByUsername(mUsername);

                    int index = mUserList.indexOf(mUserLogin);
                    if (index >= 0) {
                        //update list with edited user to ensure recycler view updates
                        mUserList.set(index, mEditedUserLogin);

                        //notify adapter to ensure recycler view updates
                        notifyItemChanged(position);

                    }

                }
            });


            // OnClick method to delete individual daily weight entries

            mDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // delete from database
                    mWeightTrackerDB.deleteUser(mUsername);

                    int index = mUserList.indexOf(mUserLogin);

                    if (index >= 0) {
                        // remove from list to ensure recycler view updates
                        mUserList.remove(index);

                        //notify adapter to ensure recycler view updates
                        notifyItemRemoved(position);
                    }

                }
            });

        }


    }
}

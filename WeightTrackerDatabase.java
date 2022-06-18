// This file defines the WeightTrackerDatabase which is a SQLite database that is responsible for storing
//   all user information, including user login data, goal weights, and daily weights. This file also
//     defines methods that may be performed on or with this data, which allows for successful interaction
//        between the database and the user.

package com.zybooks.cs360finalproject_aleksbevz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;



public class WeightTrackerDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 7;
    private static final String DATABASE_NAME = "userLogin.db";
    private static final String TAG = "WeightTrackerDatabase";

    private static WeightTrackerDatabase mWeightTrackerDB;

    public static WeightTrackerDatabase getInstance(Context context) {
        if (mWeightTrackerDB == null) {
            mWeightTrackerDB = new WeightTrackerDatabase(context);
        }
        return mWeightTrackerDB;
    }

    private WeightTrackerDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //define user table with a column for each UserLogin parameter
    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
        private static final String COL_PHONE = "phone";
        private static final String COL_SMS = "sms";
        private static final String COL_ALERT_EDITED_DW = "alert_edited_dw";
        private static final String COL_USER_ROLE = "user_role";
    }

    //define goal weight table with a column for each GoalWeight parameter
    private static final class GoalWeightTable {
        private static final String TABLE = "goal_weight";
        private static final String COL_USERNAME = "username";
        private static final String COL_GOAL_WEIGHT = "goal_weight";

    }

    //define daily weight table with a column for each DailyWeight parameter
    private static final class DailyWeightTable {
        private static final String TABLE = "daily_weight";
        private static final String COL_ID = "_id";
        private static final String COL_DATE = "date";
        private static final String COL_WEIGHT = "weight";
        private static final String COL_USERNAME = "username";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the User table; username is primary key to ensure unique logins
        // Enhancement 2 - for SMS notifications, added columns for SMS and AlertEditedDW
        // Enhancement 3 - added user role column
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_USERNAME + " primary key, " +
                UserTable.COL_PASSWORD + ", " +
                UserTable.COL_PHONE + ", " +
                UserTable.COL_SMS + ", " +
                UserTable.COL_ALERT_EDITED_DW + ", " +
                UserTable.COL_USER_ROLE + " )");

        // Create goal weight table with username as primary key to ensure each user only
        //  has one goal weight at a time
        // added foreign key that cascade deletes (when user is deleted, their goal weight will be deleted)
        db.execSQL("create table " + GoalWeightTable.TABLE + " (" +
                GoalWeightTable.COL_USERNAME + " primary key, " +
                GoalWeightTable.COL_GOAL_WEIGHT + ", " +
                "foreign key(" + GoalWeightTable.COL_USERNAME + ") references " +
                UserTable.TABLE + "(" + UserTable.COL_USERNAME + ") on delete cascade)");

        // Create daily weight table with foreign key that cascade deletes (when user is deleted, their daily weights will be deleted)
        db.execSQL("create table " + DailyWeightTable.TABLE + " (" +
                DailyWeightTable.COL_ID + " integer primary key autoincrement, " +
                DailyWeightTable.COL_DATE + ", " +
                DailyWeightTable.COL_WEIGHT + ", " +
                DailyWeightTable.COL_USERNAME + ", " +
                "foreign key(" + DailyWeightTable.COL_USERNAME + ") references " +
                UserTable.TABLE + "(" + UserTable.COL_USERNAME + ") on delete cascade)");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        db.execSQL("drop table if exists " + GoalWeightTable.TABLE);
        db.execSQL("drop table if exists " + DailyWeightTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                db.execSQL("pragma foreign_keys = on;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    // Enhancement 3, Part 2: Add XOR encryption to usernames and passwords.
    //  This method is called to encrypt when saving to tables, and decrypt when pulling from tables
    public String encryptOrDecrypt(String input) {
        String key = "Secret";
        int inputLength = input.length();
        int keyLength = key.length();
        String output = "";

        //ensure input has content
        assert(inputLength > 0);

        //apply the encryption
        for (int i=0; i < inputLength; i++){
            output = output + ((char) (input.charAt(i) ^ key.charAt(i % keyLength)));
        }

        //encrypted input is returned as "output"
        return output;

    }


    // Add a new user to the database
    // Enhancement 2 - added SMS and Alert Edited DW columns for SMS notifications
    // Enhancement 3 - added User Role column due to addition role-based access
    public boolean addUser(UserLogin userLogin) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, encryptOrDecrypt(userLogin.getUsername()));   // store username encrypted
        values.put(UserTable.COL_PASSWORD, encryptOrDecrypt(userLogin.getPassword()));   // store password encrypted
        values.put(UserTable.COL_PHONE, userLogin.getPhoneNumber());
        values.put(UserTable.COL_SMS, userLogin.getReceiveSMS());
        values.put(UserTable.COL_ALERT_EDITED_DW, userLogin.getAlertEditedDW());
        values.put(UserTable.COL_USER_ROLE, userLogin.getUserRole());
        long id = db.insert(UserTable.TABLE, null, values);
        return id != -1;
    }

    // Get username and password for authentication checks
    public boolean getUserByCredentials(String username, String password) {
        boolean credentialsFound;
        SQLiteDatabase db = this.getReadableDatabase();

        //need to encrypt credentials to search database for a match
        String encryptedUsername = encryptOrDecrypt(username);
        String encryptedPassword = encryptOrDecrypt(password);

        String sql = "select * from " + UserTable.TABLE + " where username = ? and password = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { encryptedUsername, encryptedPassword });
        if (cursor.moveToFirst()) {
            credentialsFound = true;
        }
        else {
            credentialsFound = false;
        }
        cursor.close();

        return credentialsFound;
    }

    // Get the user data by username only - used for SMS permission/sending feature
    // Enhancement 2 - added last two columns for SMS notifications
    public UserLogin getUserByUsername(String username) {
        UserLogin userLogin = null;

        //need to encrypt username to search database for a match
        String encryptedUsername = encryptOrDecrypt(username);

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + UserTable.TABLE + " where "
                + UserTable.COL_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { encryptedUsername });

        if (cursor.moveToFirst()) {
            userLogin = new UserLogin();
            userLogin.setUsername(encryptOrDecrypt(cursor.getString(0))); // decrypt username (so we can get it from returned userLogin object)
            userLogin.setPassword(cursor.getString(1));                   // leave PW encrypted (security)
            userLogin.setPhoneNumber(cursor.getString(2));
            userLogin.setReceiveSMS(cursor.getString(3));
            userLogin.setAlertEditedDW(cursor.getLong(4));
            userLogin.setUserRole(cursor.getString(5));
        }
        cursor.close();
        return userLogin;
    }

    // Enhancement 3, Part 2 - role based access
    // added method to generate list used in recycler view in Admin Activity
    public List<UserLogin> getUserList() {
        UserLogin userLogin;
        List<UserLogin> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from " + UserTable.TABLE;
        Cursor cursor = db.rawQuery(sql, new String[] {});
        if (cursor.moveToFirst()){
            do {
                userLogin = new UserLogin();
                userLogin.setUsername(encryptOrDecrypt(cursor.getString(0))); // decrypt username (so we can get it from returned userLogin object)
                userLogin.setPassword(cursor.getString(1));                   // leave PW encrypted (security)
                userLogin.setPhoneNumber(cursor.getString(2));
                userLogin.setReceiveSMS(cursor.getString(3));
                userLogin.setAlertEditedDW(cursor.getLong(4));
                userLogin.setUserRole(cursor.getString(5));
                userList.add(userLogin);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    // Updates user login information
    // Enhancement 2 - added last two columns for SMS notifications
    public void updateUser(UserLogin userLogin) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // get username from the user login object so it can be encrypted before updating the user in database
        String username = userLogin.getUsername();
        String encryptedUsername = encryptOrDecrypt(username);

        values.put(UserTable.COL_USERNAME, encryptedUsername);           //ensure username is encrypted
        //values.put(UserTable.COL_PASSWORD, userLogin.getPassword());   //removing this line as it is not needed
        values.put(UserTable.COL_PHONE, userLogin.getPhoneNumber());
        values.put(UserTable.COL_SMS, userLogin.getReceiveSMS());
        values.put(UserTable.COL_ALERT_EDITED_DW, userLogin.getAlertEditedDW());
        values.put(UserTable.COL_USER_ROLE, userLogin.getUserRole());
        db.update(UserTable.TABLE, values,
                UserTable.COL_USERNAME + " = ?", new String[] { encryptedUsername });
    }

    // Enhancement 3, Part 1 - implemented for admin activities (deletes user from the database)
    public void deleteUser(String username) {
        SQLiteDatabase db = getWritableDatabase();

        // encrypt username to search db for a match
        String encryptedUsername = encryptOrDecrypt(username);

        db.delete(UserTable.TABLE,
                UserTable.COL_USERNAME + " = ?", new String[] { encryptedUsername });
    }


    // For adding a goal weight to the goal weight table
    public boolean addGoalWeight(GoalWeight goalWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // get username from the goal weight object so it can be encrypted before updating the user in database
        String username = goalWeight.getUsername();
        String encryptedUsername = encryptOrDecrypt(username);

        values.put(GoalWeightTable.COL_USERNAME, encryptedUsername);   //store username encrypted
        values.put(GoalWeightTable.COL_GOAL_WEIGHT, goalWeight.getGoalWeightValue());
        long id = db.insert(GoalWeightTable.TABLE, null, values);
        return id != -1;
    }

    // Pulls the Goal Weight data from db table
    public GoalWeight getGoalWeight(String username) {
        GoalWeight goalWeight = null;

        // encrypt username to search database for a match
        String encryptedUsername = encryptOrDecrypt(username);

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + GoalWeightTable.TABLE + " where "
                + GoalWeightTable.COL_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { encryptedUsername });

        if (cursor.moveToFirst()) {
            goalWeight = new GoalWeight();
            goalWeight.setUsername(encryptOrDecrypt(cursor.getString(0)));    // decrypt username (so we can get it from returned goalWeight object)
            goalWeight.setGoalWeightValue(cursor.getString(1));
        }
        cursor.close();
        return goalWeight;
    }


    // Updates a user's existing goal weight
    public void updateGoalWeight(GoalWeight goalWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // get username from the goal weight object so it can be encrypted before updating the goal weight in database
        String username = goalWeight.getUsername();
        String encryptedUsername = encryptOrDecrypt(username);

        values.put(GoalWeightTable.COL_USERNAME, encryptedUsername);
        values.put(GoalWeightTable.COL_GOAL_WEIGHT, goalWeight.getGoalWeightValue());
        db.update(GoalWeightTable.TABLE, values,
                GoalWeightTable.COL_USERNAME + " = ?", new String[] { encryptedUsername });
    }


    // Delete a user's goal weight
    public void deleteGoalWeight(String username) {
        SQLiteDatabase db = getWritableDatabase();

        // encrypt username to search database for a match
        String encryptedUsername = encryptOrDecrypt(username);

        db.delete(GoalWeightTable.TABLE,
                GoalWeightTable.COL_USERNAME + " = ?", new String[] { encryptedUsername });
    }


    // Adds a new daily weight to the daily weight table for a given user
    public boolean addDailyWeight(DailyWeight dailyWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // get username from the daily weight object so it can be encrypted before updating the daily weight in database
        String username = dailyWeight.getUsername();
        String encryptedUsername = encryptOrDecrypt(username);

        values.put(DailyWeightTable.COL_DATE, dailyWeight.getDate());
        values.put(DailyWeightTable.COL_WEIGHT, dailyWeight.getDailyWeight());
        values.put(DailyWeightTable.COL_USERNAME, encryptedUsername);            //store username encrypted
        long id = db.insert(DailyWeightTable.TABLE, null, values);
        dailyWeight.setId(id);

        return id != -1;
    }

    // Get the Daily Weight data from db table
    public List<DailyWeight> getDailyWeights(String username) {
        List<DailyWeight> dailyWeights = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // encrypt username to search database for a match
        String encryptedUsername = encryptOrDecrypt(username);

        String sql = "select * from " + DailyWeightTable.TABLE + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { encryptedUsername });
        if (cursor.moveToFirst()) {
            do {
                DailyWeight dailyWeight = new DailyWeight();
                dailyWeight.setId(cursor.getLong(0));
                dailyWeight.setDate(cursor.getString(1));
                dailyWeight.setDailyWeight(cursor.getString(2));
                dailyWeight.setUsername(encryptOrDecrypt(cursor.getString(3)));   //decrypt username so we can get it from returned list
                dailyWeights.add(dailyWeight);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dailyWeights;
    }

    // Pulls one Daily Weight data from db table
    // Added for Enhancement 1, Part 1 - to properly update daily weights in recycler view when Edit button is clicked
    public DailyWeight getDailyWeight(long id) {
        DailyWeight dailyWeight = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + DailyWeightTable.TABLE + " where "
                + DailyWeightTable.COL_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { Float.toString(id) });

        if (cursor.moveToFirst()) {
            dailyWeight = new DailyWeight();
            dailyWeight.setId(cursor.getLong(0));
            dailyWeight.setDate(cursor.getString(1));
            dailyWeight.setDailyWeight(cursor.getString(2));
            dailyWeight.setUsername(encryptOrDecrypt(cursor.getString(3)));   //decrypt username so we can get it from returned object
        }
        cursor.close();
        return dailyWeight;
    }

    // Update a daily weight entry
    public void updateDailyWeight(DailyWeight dailyWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // get username from the daily weight object so it can be encrypted before updating the daily weight in database
        String username = dailyWeight.getUsername();
        String encryptedUsername = encryptOrDecrypt(username);

        values.put(DailyWeightTable.COL_ID, dailyWeight.getId());
        values.put(DailyWeightTable.COL_DATE, dailyWeight.getDate());
        values.put(DailyWeightTable.COL_WEIGHT, dailyWeight.getDailyWeight());
        values.put(DailyWeightTable.COL_USERNAME, encryptedUsername);           //store username encrypted
        db.update(DailyWeightTable.TABLE, values,
                DailyWeightTable.COL_ID + " = " + dailyWeight.getId(), null);
    }


    // Delete a daily weight entry
    public void deleteDailyWeight(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DailyWeightTable.TABLE,
                DailyWeightTable.COL_ID + " = " + id, null);
    }


}

# Aleks Bevz ePortfolio Introduction
 
# Project Code Review

This code review was performed at the beginning of this course, prior to making any of the enhancements. This review highlights some issues with the starting code and describes the plans for each enhancement.

# Enhancement 1: Software Design and Engineering

## Enhancement 1 Narrative:

For Enhancement 1, I made updates to the UI of my Weight Tracker application which I developed in November and December 2021 for course CS360 at SNHU. I wanted to include this artifact in my portfolio because it showcases my skills in all three categories for the final project. For software development specifically, it shows that I can create an application that meets the user requirements and has an appealing interface. In this enhancement,  I made the UI look more professional and visually appealing; I also fixed the recycler view issue in WeightDisplayActivity.java (the daily weights did not update or delete properly). This shows that I am able to troubleshoot and resolve issues with my work. 

I did meet the course objectives with this enhancement, as I used my skills to update the UI and to solve logic issues. Specifically, I met the following course outcomes:

* CS-499-01: Employ strategies for building collaborative environments that enable diverse audiences to support organizational decision making in the field of computer science
    * I met this outcome in my code review, as I showed that I was able to create a thorough code review experience. In this enhancement, I also met this outcome by providing inline comments that provide context to help others understand the code’s purpose and the decision-making that went into the application. 
*	CS-499-02: Design, develop, and deliver professional-quality oral, written, and visual communications that are coherent, technically sound, and appropriately adapted to specific audiences and contexts
    * I met this outcome by detailing my experience within this narrative as I explain clearly and concisely my decision making process when working on this enhancement.
*	CS-499-04:  Demonstrate an ability to use well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals
    *	I met this outcome by using my skills to create a solution that resolved my recycler view issues, and I also improved the overall design of my application to make it more professional and appealing. I also used my computer science skills to incorporate some security, which is discussed next.
* CS-499-05: Develop a security mindset that anticipates adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources
        * I met this outcome by incorporating input validation into my application. I did this by setting a maxLength value for these fields in the layout files. For date in DailyWeightActivity, I added a date picker and set the date field so that it can only be clicked but not typed into. This means the user cannot enter anything into the fields except for a date they choose from the date picker.
    
While I was making my enhancements, I learned a lot. While I was doing my code review, I was able to pinpoint where certain issues were, so I had a general idea of what I needed to fix. I first fixed the recycler view issue by adding a line of code to RecyclerViewAdapter.java which was required to get the instance of the weight tracker database, so I added this line. For deleting daily weights, the onClick method for the delete button (in RecyclerViewAdapter.java) only had code to delete the daily weight from the database. I needed to add code to also remove it from the List that is used to generate the recycler view display. For editing daily weights, I fixed the following additional problems:

* In the onClick method for the edit button (in RecyclerViewAdapter.java), I was passing the username as an extra to the intent that launched the EditDailyWeightActivity, but I should have been using the ID of the daily weight. I changed this so that I could be sure the correct daily weight entry would be updated.
* In EditDailyWeightActivity.java, I was incorrectly creating a new daily weight object instead of updating the existing one. 
    * I fixed this by getting the existing object using the WeightTrackerDatabase.java getDailyWeight() method (created for this purpose), then updating the date and weight of the same object using the setDate() and setWeight() methods of the DailyWeight class. Finally, I used the WeightTrackerDatabase.java updateDailyWeight() method to update the daily weight entry in the database. 
    * **NOTE:** This file no longer exists due to changes made in Enhancement 2, but the code and code fixes were incorporated into DailyWeightActivity. 
* In the onClick method for the edit button (in RecyclerViewAdapter.java), I added code that was previously missing and was needed to:
    * get the updated daily weight entry from the database using the getDailyWeight() method of WeightTrackerDatabase.java
    * update the daily weight in the List that is used to generate the recycler view display
        
I also added a new color scheme which has more muted colors to appeal to a wider variety of users. I also updated the font and made the layouts look cleaner by removing certain unnecessary TextView fields (for example, on the login screen, I removed the “Username:” and “Password:” TextView fields and instead put a hint into the edit texts that say “Enter username” or “Enter password”). This makes the layout look a lot cleaner. Additionally, I added more dimension to the appearance by making the button corners more rounded, and by adding a lighter-colored, rounded square behind some text fields and image buttons. To further clean up the login screen, I removed a TextView field that was being used to display a message for successful registrations, unsuccessful registrations, and invalid credentials. I added dialog fragments to display these messages instead. 

Here are some images of the new UI:
* [LoginRegisterActivity](./LoginRegisterActivity.png)
* [LoginRegisterActivity showing one example of a dialog message](./LoginRegisterActivity_dialog.png)
* [WeightDisplayActivity](./WeightDisplayActivity.png)
* [GoalWeightActivity](./GoalWeightActivity.png)
* [DailyWeightActivity](./DailyWeightActivity.png)
* [DailyWeightActivity date picker](./DailyWeightActivity_date_picker.png)
* [smsPermissionActivity](./smsPermissionActivity.png)
            

## Highlights of Enhancement 1:

In [RecyclerViewAdapter.java](./RecyclerViewAdapter.java), the following code highlights issues that were previously preventing the recycler view from updating when a daily weight was edited. The comments explain how the issues were resolved:

```java
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
```

Similar issues existed when attempting to delete a daily weight: 

```java
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
```

Previously, the method in EditDailyWeightActivity that was responsible for updating an existing daily weight entry did not properly update the existing daily weight object. A new object was being created instead. Here the below code in [DailyWeightActivity.java](./DailyWeightActivity.java) shows that for edited daily weights, the existing object is updated and then saved to the database again: 

```java
    // Saves the new/edited daily weight
    public void onSaveClick(View view) {
        //add new daily weight if user is adding data
        if (mLaunchingAction.equals("add")){
            
            ...
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
```

# Enhancement 2: Algorithms and Data Structure

## Enhancement 2 Narrative:

For Enhancement 2, I fixed an issue in my Weight Tracker application which I developed in November and December 2021 for course CS360 at SNHU. I wanted to include this artifact in my portfolio because it showcases my skills in all three categories for the final project. One of the requirements for this application was that it would allow a user to opt in to receive SMS notifications when they reached their goal weight. When I first created this project, the SMS notification feature did not work, so I decided to fix it for this enhancement.  This required that I update my application to include more complex algorithms and data structures that would be responsible for 1) checking the user has opted in for SMS notifications, and 2) checking if the user has reached their goal weight. 

Three issues with my original project were that (1) I was using a deprecated method to launch the smsPermissionActivity from WeightDisplayActivity; (2) the compareGoalWeight() method in WeightDisplayActivity looped through all of a user’s daily weights to check if the goal weight had been reached; (3) the Android Manifest was missing the lines that allow it to send SMS messages. In my original project, my intention was to get the user’s response (yes or no) back from smsPermissionActivity, then if the user’s response was yes, to loop through all of that user’s daily weights and send an SMS if one of them was equal to the goal weight. The problem with this approach is that the user’s response would not be associated with that specific user (the variable storing this information was not associated with a UserLogin object), and that the user would receive multiple SMS messages for any single daily weight that equaled the goal weight (due to the loop).

I fixed the first issue by replacing the deprecated startActivityForResult method with an ActivityResultLauncher in WeightDisplayActivity.java. Instead of launching and trying to get a result back from smsPermissionActivity, I chose to launch DailyWeightActivity. This way, once DailyWeightActivity ended, the onActivityResult() method in the ActivityResultLauncher would execute. This method checks to see if the user has allowed SMS notifications. This information is stored in a new parameter of the UserLogin class, receiveSMS. By default, this variable is set to “no” when a user is created. If the user chooses “yes” in smsPermissionActivity, this value is then changed to “yes” and the user is updated in the database. 

Whenever a user finishes adding a new daily weight, onActivityResult() method in the ActivityResultLauncher will execute. If the user has allowed SMS notifications, then compareGoalWeight() executes. One challenge I faced was getting this method to execute after a user edited a daily weight. I wanted the SMS notification feature to work if an edited daily weight equaled the goal weight as well. So I merged EditDailyWeightActivity into DailyWeightActivity (and then deleted EditDailyWeightActivity), hoping this would resolve the issue. However, this did not work because the onActivityResult() method in WeightDisplayActivity only executes when DailyWeightActivity ends, but only if DailyWeightActivity was launched by the ActivityResultLauncher (which was only tied to the Add New Daily Weight button onClick method). I could not use an ActivityResultLauncher in RecyclerViewAdapter.java (where the edit button onClick method is for daily weights). 

Therefore, I added another parameter to the UserLogin object to check whether the user has edited a daily weight. This parameter is called alertEditedDW. When a new user is created, this variable is set to -1 (meaning no edited daily weights). After a user edits a daily weight, this value now changes to equal the Id of the edited daily weight. In OnResume() of WeightDisplayActivity, the user’s alertEditedDW value is checked. If it is not -1 (meaning a daily weight has been edited), then compareGoalWeight() executes. 

In compareGoalWeight(), there is an If-Else branch to deal with an edited daily weight versus a new daily weight. If a daily weight was edited, then the Id of the edited daily weight is used to get the daily weight value and compare it with the goal weight. If a new daily weight was added, then only the last daily weight is checked against the goal weight. In either case, if the daily weight equals the goal weight, then sendSMS() is called. In the case of an edited daily weight, the alertEditedDW variable is then set back to -1 to prevent multiple SMS messages from being sent every time onResume() executes.

With this enhancement, I met the following course outcomes: 

* CS-499-01: Employ strategies for building collaborative environments that enable diverse audiences to support organizational decision making in the field of computer science
    * I met this outcome in my code review, as I showed that I was able to create a thorough code review experience. In this enhancement, I also met this outcome by providing inline comments that provide context to help others understand the code’s purpose and the decision-making that went into the application. 
* CS-499-02: Design, develop, and deliver professional-quality oral, written, and visual communications that are coherent, technically sound, and appropriately adapted to specific audiences and contexts
    * I met this outcome by detailing my experience within this narrative as I explain clearly and concisely my decision making process when working on this enhancement.
* CS-499-03: Design and evaluate computing solutions that solve a given problem using algorithmic principles and computer science practices and standards appropriate to its solution, while managing the trade-offs involved in design choices
    * I met this outcome by solving logic problems that existed in my original code and were preventing the SMS notification feature from working as intended. Not only was I able to meet this basic user requirement, but I was able to make it apply to both new daily weights and edited daily weights.
* CS-499-04:  Demonstrate an ability to use well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals
    * I met this outcome by showing that I can be innovative, which I had to do to get the SMS notifications to work for edited daily weights as well as new daily weights. As mentioned previously, I struggled with this part and tried multiple different things which helped bring out my innovative problem solving skills. 

This enhancement was difficult mostly because I struggled with getting the SMS notifications to send when a daily weight was edited. By working through this challenge in the ways I described above, I showed that I can use innovative skills and techniques that involved making my data structures and algorithms more complex to handle different circumstances. 

Here are some images showing the SMS notifications in action:

* [A user’s **new** daily weight (6/18/2022, 220) equals the goal weight](./new_DW.png)
    * [SMS received for this](./SMS_new_DW.png)

* [A user’s **edited** daily weight (5/28/2022, 220) equals the goal weight](./edited_DW.png)
    * [SMS received for this](./SMS_edited_DW.png)


## Highlights of Enhancement 2:

With this enhancement, the [UserLogin](./UserLogin.java) data structure gained some more complexity by having two parameters added to store the permission allowance and whether the user has edited a daily weight. 

[smsPermissionActivity.java](./smsPermissionActivity.java) is responsible for allowing the user to opt in to receive SMS notifications. When a user opts in, they can enter their phone number and then click save. This saves the user's phone number and their acceptance to receive SMS notifications to the user object, which is then saved to the database: 

```java
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
```

In [WeightDisplayActivity.java](./WeightDisplayActivity.java), when a user clicks the button to add a new daily weight, the following ActivityResultLauncher is used to launch the [DailyWeightActivity](./DailyWeightActivity.java).

```java
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
```
As shown above, if the user has opted in to receive SMS notifications, compareGoalWeight() is called. This only applies to new daily weights, as the ActivityResultLauncher is only used in the onClick method for adding a new daily weight. To account for edited daily weights, the onResume() method in  [WeightDisplayActivity.java](./WeightDisplayActivity.java) was modified: 

```java
    @Override
    protected void onResume(){
        super.onResume();

        // user's goal weight and daily weights are constantly displayed
        displayGoalWeight();
        displayDailyWeight();

        //Enhancement 2 - added check to see if the user has edited a daily weight and has allowed SMS notifications
        //if yes, then compareGoalWeight() will execute
        mUserLogin = mWeightTrackerDB.getUserByUsername(mUsername); //get up to date user object
        mAlertEditedDW = mUserLogin.getAlertEditedDW();             //get flag to see if user has edited a daily weight
        String receiveSMS = mUserLogin.getReceiveSMS();             //get user's SMS permission status

        //if a daily weight has been edited and user has allowed SMS notifications
        if ((mAlertEditedDW != -1) && (receiveSMS.equals("yes"))){
            compareGoalWeight();
        }
    }
```

The compareGoalWeight() method is shown below. This method checks to see if the user has edited or added a new daily weight, and then compares either the new or edited weight to the goal weight. If they are equal, sendSMS() is called to send the text message.  

```java
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

```

# Enhancement 3: Databases

## Enhancement 3 Narrative:

For Enhancement 3, I enhanced the database in my Weight Tracker application which I developed in November and December 2021 for course CS360 at SNHU. I wanted to include this artifact in my portfolio because it showcases my skills in all three categories for the final project. For databases specifically, this artifact shows that I can construct a database to store data that is used in my application. Not only is data able to be stored, but it is also able to be accessed so that information can be displayed in the UI. This database is also dynamic because it allows data to be updated by users. For example, there are methods to add users, add goal weights, add daily weights, and to update and remove these items as well. Therefore, this artifact shows that I have skills necessary for database creation and management, particularly in SQLite.

For this enhancement, I improved the artifact in two ways. First, I made critical user information (usernames and passwords) more secure by adding XOR encryption to the database (WeightTrackerDatabase.java, encryptOrDecrypt() method). When a user is created, their credentials are encrypted when they get stored in the database. In the methods that are responsible for getting lists or objects from the database (example: getUserByUsername()), the username is decrypted so that it is in usable form in the returned object (the password stays encrypted for security reasons). This is necessary as many activities in the application use the username to make sure the data is displayed or updated/deleted for the correct user. Additionally, in methods that update/delete objects (example: updateGoalWeight()), the username is again encrypted before the object information is updated in, or deleted from, the database. 

One challenge I experienced was that some functions did not work correctly after I made these changes. For example, the user information was not updated appropriately when I tried to add SMS permissions for one user. I realized that I forgot to change one of the db.update() commands to use the encrypted username in WeightTrackerDatabase.java (specifically the updateUser() method. This was a simple fix, but it was a good reminder to move slowly to avoid errors. 

For the second improvement, I added the feature of role-based access so that there could be an admin role that is able to have more control over the database by being able to update and delete users. This required adding a userRole parameter to the UserLogin class, as well as an additional column to the users table in the database. In LoginRegisterActivity, the default admin is created (if it does not already exist) through the createAdminUser() method. I also had to create a couple of activities for the admin to be able to manage users. In DisplayDataActivity, there is a check for user role, and if the user role is adminUser, then the admin icon is visible in the app bar. Once the admin icon is clicked, AdminActivity launches. This activity shows a recycler view of current users, and each user has an edit and a delete button. If the edit button is clicked, then the EditUserActivity launches, which allows the admin to change the user’s role. 

While I was making this update, I learned to enhance my security by making the edit and delete buttons invisible for the default admin. This is because other admins could delete or edit this user. If the default admin was deleted, then it would just be recreated upon startup of the LoginRegisterActivity. However, there wasn’t a safeguard in place for ensuring the user role would remain adminUser, so I just decided it would be best to remove the edit and delete options altogether. 

With this enhancement, I did meet the course outcomes of:

* CS-499-01: Employ strategies for building collaborative environments that enable diverse audiences to support organizational decision making in the field of computer science
    * I met this outcome in my code review, as I showed that I was able to create a thorough code review experience. In this enhancement, I also met this outcome by providing inline comments that provide context to help others understand the code’s purpose and the decision-making that went into the application. 
* CS-499-02: Design, develop, and deliver professional-quality oral, written, and visual communications that are coherent, technically sound, and appropriately adapted to specific audiences and contexts
    * I met this outcome by detailing my experience within this narrative as I explain clearly and concisely my decision making process when working on this enhancement.    
* CS-499-04:  Demonstrate an ability to use well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals   
   * I met this outcome by showing that I am able to use the well-founded technique of XOR encryption. This is an important industry-specific goal because it provides security of sensitive customer information. I also attempted some innovative techniques when designing my administrator user. Although it is not a new concept, it is not something I have done before and I was able to figure out how to make this kind of user that has more oversight over the database. 
* CS-499-05: Develop a security mindset that anticipates adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources    
    * I met this outcome because this enhancement provides more the security over the database. With the creation of the admin user specifically, I mitigated the design flaw of allowing other admins to delete or edit the default admin by removing those options entirely.

Here are some screenshots showing the improvements made in Enhancement 3:

* A look at the [Database Inspector](./encrypted_database.png), which shows all usernames and passwords are encrypted
* [WeightDisplayActivity](./admin_icon.png) for a user with adminUser role; the admin icon is present in the app bar
* When the admin icon is clicked, [AdminActivity](./AdminActivity.png) launches, showing a list of users. Each user has an edit and delete button
* When the Edit button is clicked for a user, [EditUserActivity](./EditUserActivity.png) launches and allows the admin to change the user’s role
* When a user role is selected, the button becomes [disabled to show the selection and the Save button appears](./EditUserActivity_select_role.png). When Save is clicked, the user role becomes updated


## Highlights of Enhancement 3:

The following code was added to [WeightTrackerDatabase.java](./WeightTrackerDatabase.java) to create a method for encrypting usernames and passwords: 

```java
    // Enhancement 3: Add XOR encryption to usernames and passwords.
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
```

As seen in one example below, this method is called when saving usernames and passwords to the Users table in the database: 

```java
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
```

To add an administrator role, [LoginRegisterActivity.java](./LoginRegisterActivity.java) was modified to create a default admin if one does not already exist:

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        ...

        //get instance of weight tracker db
        mWeightTrackerDB = WeightTrackerDatabase.getInstance(getApplicationContext());

        ...

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
```

Additionally, [AdminActivity.java](./AdminActivity.java) and [EditUserActivity.java](./EditUserActivity.java) were created to provide the administrative functions of editing and deleting users. AdminActivity uses the recycler view generated by [AdminRecyclerViewAdapter.java](./AdminRecyclerViewAdapter.java).

In order to restrict admin functions to only administrative users, the following code was added to [WeightDisplayActivity.java](./WeightDisplayActivity.java), which only provides the admin icon to admin users:

```java
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
```


### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://support.github.com/contact) and we’ll help you sort it out.

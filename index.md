## Aleks Bevz ePortfolio Introduction
 
## Project Code Review

This code review was performed at the beginning of this course, prior to making any of the enhancements. This review highlights some issues with the starting code and describes the plans for each enhancement.

## Enhancement 1: Software Design and Engineering

### Enhancement 1 Narrative:

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
            

### Enhancement 1 Overview:


## Enhancement 2: Algorithms and Data Structure

### Enhancement 2 Narrative:

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


### Enhancement 2 Overview:


## Enhancement 3: Databases

### Enhancement 3 Narrative:


### Enhancement 3 Overview:

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://support.github.com/contact) and we’ll help you sort it out.

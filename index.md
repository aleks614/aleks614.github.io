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
* [LoginRegisterActivity](./Enh1 - LoginRegisterActivity.png)
* [LoginRegisterActivity showing one example of a dialog message] (./Enh1 - LoginRegisterActivity dialog.png)
* [WeightDisplayActivity](./Enh1 - WeightDisplayActivity.png)
* [GoalWeightActivity](./Enh1 - GoalWeightActivity.png)
* [DailyWeightActivity](./Enh1 - DailyWeightActivity.png)
* [DailyWeightActivity date picker](./Enh1 - DailyWeightActivity date picker.png)
* [smsPermissionActivity](./Enh1 - smsPermissionActivity.png)
            

### Enhancement 1 Overview:


## Enhancement 2: Algorithms and Data Structure

### Enhancement 2 Narrative:


### Enhancement 2 Overview:


## Enhancement 3: Databases

### Enhancement 3 Narrative:


### Enhancement 3 Overview:

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://support.github.com/contact) and we’ll help you sort it out.

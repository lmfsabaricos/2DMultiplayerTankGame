 Introduction

**Project Overview**
The Tank game is an ideal term project because it entails both designing and implementing a fascinating game from start to finish.. The main goal of the game is to achieve fast, challenging gameplay for two players where both of them control tanks and the field is full of barriers. This project also provides real-world ideas related to object-oriented programming and game development as well as resource optimization and use of libraries. It is a focal point of competitive play where the two players are engaged in an ongoing fight.
**Introduction of the Tank Game**
The Tank game is a 2D arcade shooting game in which players can enjoy the real intense battle experience. Players control a tank, moving it across a map and fighting against the opposite tank. The goal is to fire at the enemy tank and do not allow them to shoot back at you. The game ends once either tank loses their lives completely. Through this mechanism, gameplay remains stimulating and rewarding for the player in the process of completing a level.
**3. Development Environment**
Version of Java Used
Java SE Development Kit 8u271
IDE Used
IntelliJ IDEA Community Edition 2020.3
Special Libraries and Resources
LibGDX for game development: LibGDX
Tiled for map creation: Tiled Map Editor


**4. How to Build or Import Your Game in the IDE**
Importing the Project
Download and Install IntelliJ IDEA:
If you haven't already, download and install IntelliJ IDEA
Clone the Project from GitHub:
Open terminal or Git Bash.
Navigate to the directory where you want to clone the project (e.g., cd Documents).
Clone the repository using the following command: git clone
Open IntelliJ IDEA:
Launch IntelliJ IDEA
Import the Project:
Click on New Project from Existing Sources.
Navigate to the cloned project directory
Select the project folder and click OK.
Wait for IntelliJ to Set Up the Project:
IntelliJ IDEA will index the project and download any necessary dependencies.

_**Building the JAR**_
Open Your Project:
Launch IntelliJ IDEA and open the Tank Game project.
Open Project Structure:
Go to File > Project Structure.
Configure Artifacts:
In the Project Structure dialog, select Artifacts from the left-hand menu.
Click the + button to add a new artifact.
Select JAR > From modules with dependencies.
Setup the JAR Configuration:
In the dialog that appears, select your main module (e.g., tankgamepack).
Make sure to specify the Main-Class (e.g., tankgamepack.Launcher).
Click OK.
Include Resources:
Ensure that any resources (like images, sounds, etc.) that your application needs are included in the JAR.
 We can do this by adding them to the Available Elements section in the Output Layout tab.
Build the JAR:
Once the artifact is set up, go to Build > Build Artifacts.
Select the artifact you just created (e.g., tank gamepack:jar) and click Build.
IntelliJ IDEA will build the JAR file and place it in the out/artifacts directory of your project.

_**Running the Built JAR**_
Navigate to the Directory:
Open a terminal or command prompt.
Navigate to the directory where your JAR file is located. This is typically in the out/artifacts directory of your project.”in my case”  (cd path/to/your/project/out/artifacts

Run the JAR File:
Use the java -jar command to run your JAR file. Replace yourjarfile.jar with the name of your JAR file. (java -jar yourjarfile.jar)


# SessionTask

Installation and Execution Instructions for SessionTask Project

General Information

1) The Project sessiontask is a Scala project which is developed in Scala IDE. 
2) Maven is used as a build tool for this project. Scala-maven eclipse plugin is used for this purpose.
3) The project can be imported within Eclipse using import as Maven project option.
4) SessionTask.scala is the Scala file where all the business logic has been implemented
5) SessiontaskTest.scala if the file where test cases has been written. 

Building the Project
1) Right click on project's pom.xml and go to Run As option. Then chose maven clean option to clear the existing target
2) Right click again on project's pom.xml and go to Run As option. Then chose maven install option and mention "clean package" in Goals and click Run. Run option will compile the project, run the test cases and build the package
3) After step 2, go to project root, right click and click Refresh Option. Now target directory will be visible inside the project.
4) Inside target directory, there are 2 jar files
•	sessiontask-1.0.jar
•	sessiontask-1.0-jar-with-dependencies.jar

Running the Test cases
1) Inside Scala IDE, go to project root and traverse through src -> test -> scala -> samples -> junit.scala
2) Right click on junit.scala and go to Run As option. Now choose Scala Junit test. It will run the junit test cases and show if its success or failure.

Running the Project
SessionTask project can be run in two ways.
1) Inside scala IDE, go to project root and traverse through src -> main -> scala -> verto.sessiontask -> SessionTask.scala. Right click on SessionTask.scala and go to Run As option. Now choose Scala Application. It will run the main method which will ask for user's input for the file.
2) We can run the project from command line as well. Go to Project directory in your file system, traverse to the target directory (or if you already pasted sessiontask-1.0-jar-with-dependencies.jar file in your other directory in file system then go there) and please run below command for running the project:-
	java -jar sessiontask-1.0-jar-with-dependencies.jar
	

##DELIVERY FEE CALCULATOR
_____

### Problems:
Problem when configuring root project - This project uses Java 17, make sure your Java and Gradle JVM is set to
17 or higher.

In Intellij you can change it under:  
`File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM` 

After this the project should configure correctly.  
Go to `build.gradle` and on the right top corner there's a gradle reload button or use `Ctrl+Shift+O`
____
### Running the project:
`.\gradlew clean build bootRun`  

Now go to `localhost:8080` and you can start calculating delivery fees.

____
### Tests
To run tests you can use `.\gradlew clean test`   
or to see coverage in Intellij right-click on `src/test > More Run/Debug > Run ... With Coverage` 
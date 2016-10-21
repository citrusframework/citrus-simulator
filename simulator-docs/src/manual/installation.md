## Installation

The Citrus simulator is started as a web application that uses Spring Boot. The easiest way to get started is to use a Maven archetype that creates a new
project for you.

    > mvn archetype:generate -DarchetypeGroupId=com.consol.citrus -DarchetypeArtifactId=citrus-simulator-quickstart

If you execute the command above the Maven archetype generator will ask you some questions about versions and project names. Once you have completed the generation
you get a new Maven project that is ready to use. The project is created in a new folder on your machine. Switch to that folder and continue to build the project.

### Build project

You can directly build the new project with

    > mvn install
    
This compiles, tests and packages the project. Now we can run the simulator:
    
    > mvn spring-boot:run
    
You will see the application starting up. Usually you will see some console log output. The web server should start within seconds. Once the application is up and running
you can open your browser and point to ***http://localhost:8080***.
     
Now everything is set up and you can start to create some simulator scenarios.     
    
### Use simulator artifacts    

The simulator is also available as packages web application artifact. First of all download the latest distribution which
is a Java JAR file located at [labs.consol.de/maven/repository](https://labs.consol.de/maven/repository/com/consol/citrus/citrus-simulator):

Save the Java archive to a folder on your local machine and start the Spring boot web application. The downloaded JAR should be executable 
from command line like this:

```java -jar citrus-simulator-0.7.jar```

You will see the application starting up. Usually you will see some console log output. The web server should start within seconds. Once the application is up and running
you can open your browser and point to ***http://localhost:8080***.
 
That's it you are ready to use the Citrus simulator.
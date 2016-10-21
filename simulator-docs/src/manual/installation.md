## Installation

The Citrus simulator is started as a web application that uses Spring Boot. The easiest way to get started is to use a Maven archetype that creates a new
project for you.

```
mvn archetype:generate -DarchetypeGroupId=com.consol.citrus.archetypes -DarchetypeArtifactId=citrus-simulator-archetypes-rest
```

If you execute the command above the Maven archetype generator will ask you some questions about versions and project names. Once you have completed the generation
you get a new Maven project that is ready to use. The project is created in a new folder on your machine. Switch to that folder and continue to build the project.

There are different simulator archetypes available. Please pick the most convenient archetype according to your project purpose.

* **citrus-simulator-archetypes-rest** Http REST simulator sample
* **citrus-simulator-archetypes-ws** SOAP web service simulator sample
* **citrus-simulator-archetypes-jms** JMS simulator sample

### Build project

You can directly build the new project with

```
mvn install
```
    
This compiles, tests and packages the project. Now we can run the simulator:
   
```
mvn spring-boot:run
```

You will see the application starting up. Usually you will see some console log output. The web server should start within seconds. Once the application is up and running
you can open your browser and point to ***http://localhost:8080***.
     
Now everything is set up and you can start to create some simulator scenarios.     
    
### Use simulator artifacts    

The simulator project creates a web application artifact. After building you can find this WAR file in target/citrus-simulator-1.0.war

Name and version of that archive file may be different according to your project settings. You can start the simulator with Java

```
java -jar citrus-simulator-1.0.war
```

You will see the application starting up. Usually you will see some console log output. The web server should start within seconds. 
Once the application is up and running you can open your browser and point to ***http://localhost:8080***.
 
That's it you are ready to use the Citrus simulator.
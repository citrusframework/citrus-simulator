## Preconditions

The Citrus simulator requires some software installed on your localhost.

### Java 8

The simulator is a Java application coded in Java 8. Following from that you need at least Java 8 to run it as a Spring Boot web application. 
Please make sure that you have Java development kit installed and set up. You can verify this with this command in a new terminal window.

    > java -version

### Build tools

The simulator uses Maven as build tool. If you only want to run a distribution artifact of the simulator you are fine with just Java on your machine. In case
you want to build and maintain your simulator instance you need Maven to build your simulator application. We used Maven 3 when coding the simulator. You can verify
the Maven installation on your host with this command:

    > mvn -version
 
### Browsers

The simulator provides a small web user interface when started. You can access this web UI with your browser. As we are in an early state in this project we do not
invest much time in full cross-browser compatibility. We use Chrome and Firefox during development. So the simulator application is most likely to be 100% working 
on these two browsers. Of course other browsers might work without any limitations, too.
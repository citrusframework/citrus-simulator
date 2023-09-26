Development
---------

# Installation

After forking/cloning the source code repository from [https://github.com/citrusframework/citrus-simulator](https://github.com/citrusframework/citrus-simulator) you must build the application locally with Maven *at least once*:

```
mvn clean install
```

Add the `-DskipFrontend=false` parameter to include the `simulator-ui` into the build.

## Lombok

This will compile all classes and generate constructors as well as getters and setters using [Project Lombok](https://projectlombok.org/).

> You will have to install Project Lombok in your IDE for an active development!

# Running the Samples

You can start any of the sample simulator projects using maven: 

```
mvn -pl simulator-samples/sample-rest spring-boot:run 
```

# UI

Use the angular-cli dev-server for active development or a short round trip.

```
mvn -pl simulator-ui package -Pdevelopment
```

This will automatically compile and watch the source files (e.g. *.js, *.ts, *.css). They will be copied to the Maven target folder too. The running
spring-boot application is able to automatically grab the newly compiled sources. Just go to the browser and hit refresh to see the changes.
If you change server Java sources spring-boot automatically restarts the web application so you may just hit refresh in your browser, too.

The development server is running on its own port 4200 ([http://localhost:4200](http://localhost:4200)). To avoid cors issues an api proxy to the backend is provided out of the box. 
You can configure the proxy settings in [proxy.conf.json](citrus-admin-client/src/main/resources/static/proxy.conf.json). 

Make sure to start one of the sample projects in order to have a server running which the UI can connect to.

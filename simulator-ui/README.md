# Citrus Simulator UI

This application was generated using JHipster 8.0.0-beta.3, you can find documentation and help
at [https://www.jhipster.tech/documentation-archive/v8.0.0-beta.3](https://www.jhipster.tech/documentation-archive/v8.0.0-beta.3).

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better
development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husky, and others that
are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for
  specific blueprints configuration.
- `.jhipster/*.json` - JHipster entity configuration files

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

```
npm install
```

We use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./mvnw -pl :citrus-simulator-sample-rest -am package && ./mvnw -pl :citrus-simulator-sample-rest spring-boot:run
cd simulator-ui && npm install --cache .npm && npm start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage
dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all the scripts available to run for this project.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's turned off by default. One of the main components of a
PWA is a service worker.

The service worker initialization code is disabled by default. To enable it, uncomment the following code
in `src/main/webapp/app/app.module.ts`:

```typescript
ServiceWorkerModule.register('ngsw-worker.js', {enabled: false}),
```

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

```
npm install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following
command:

```
npm install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows
about them:
Edit [src/main/webapp/app/app.module.ts](src/main/webapp/app/app.module.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```
@import 'leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

```
ng generate component my-component
```

will generate few files:

```
create src/main/webapp/app/my-component/my-component.component.html
create src/main/webapp/app/my-component/my-component.component.ts
update src/main/webapp/app/app.module.ts
```

## Building for production

### Packaging as jar

To build the final jar and optimize the `citrus-simulator` application for production, run the following from within the
root directory of `citrus-simulator`:

```
./mvnw install -DfrontendSkip=false
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references
these new files.

## Testing

Unit tests are run by [Jest][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with
directly using `npm test` or through Maven using `./mvnw test`.

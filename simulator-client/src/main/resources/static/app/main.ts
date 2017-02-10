import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {AppModule} from "./components/application/app.module";

platformBrowserDynamic().bootstrapModule(AppModule)
    .then(success => console.log(`Bootstrap success`))
    .catch(error => console.log(error));

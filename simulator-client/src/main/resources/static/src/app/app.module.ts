import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {AppComponent} from "./components/app.component";
import {NavbarComponent} from "./components/navbar/navbar";
import {AppRoutingModule, routedComponents} from "./app-routing.module";
import {AppInfoService} from "./services/appinfo-service";
import {ConfigService} from "./services/config-service";
import {ExecutionService} from "./services/execution-service";
import {MessageService} from "./services/message-service";
import {StatusService} from "./services/status-service";
import {TestParameterControlService} from "./services/test-parameter-control-service";
import {TestService} from "./services/test-service";
import {TestExecutionList} from "./components/test-execution-list/test-execution-list";
import {TestActionList} from "./components/test-action-list/test-action-list";
import {TestParameterList} from "./components/test-parameter-list/test-parameter-list";
import {TestParameterFormComponent} from "./components/test-parameter-form/test-parameter-form";
import {TestParameterFormItemComponent} from "./components/test-parameter-form-item/test-parameter-form-item";
import {MessageListComponent} from "./components/message-list/message-list";
import {AboutComponent} from "./components/about/about";
import {HelpComponent} from "./components/help/help";
import {ExecutionStatusPipe} from "./pipes/execution-status-pipe";
import {TruncatePipe} from "./pipes/truncate-pipe";
import {ScenarioNamePipe} from "./pipes/scenario-name-pipe";
import {ActivityFilterPipe} from "./pipes/activity-filter-pipe";
import {MessageFilterPipe} from "./pipes/message-filter-pipe";

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        NavbarComponent,
        TestExecutionList,
        TestActionList,
        TestParameterList,
        TestParameterFormComponent,
        TestParameterFormItemComponent,
        MessageListComponent,
        AboutComponent,
        HelpComponent,
        ExecutionStatusPipe,
        TruncatePipe,
        ScenarioNamePipe,
        ActivityFilterPipe,
        MessageFilterPipe,
        routedComponents
    ],
    providers: [
        AppInfoService,
        ConfigService,
        ExecutionService,
        MessageService,
        StatusService,
        TestParameterControlService,
        TestService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}

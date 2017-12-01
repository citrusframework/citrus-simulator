import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule } from "@angular/common/http";
import {AppComponent} from "./components/app.component";
import {HeaderComponent} from "./components/header/header";
import {AppRoutingModule, routedComponents} from "./app-routing.module";
import {AppInfoService} from "./services/appinfo-service";
import {ActivityService} from "./services/activity-service";
import {MessageService} from "./services/message-service";
import {SummaryService} from "./services/summary-service";
import {ScenarioParameterService} from "./services/scenario-parameter-service";
import {ScenarioService} from "./services/scenario-service";
import {ScenarioExecutionList} from "./components/scenarios/execution/scenario-execution-list";
import {ScenarioActionList} from "./components/scenarios/action/scenario-action-list";
import {ScenarioParameterList} from "./components/scenarios/parameter/scenario-parameter-list";
import {ScenarioParameterFormComponent} from "./components/scenarios/parameter/scenario-parameter-form";
import {ScenarioParameterFormItemComponent} from "./components/scenarios/parameter/scenario-parameter-form-item";
import {MessageListComponent} from "./components/messages/list/message-list";
import {MessageHeaderList} from "./components/messages/header/message-header-list";
import {AboutComponent} from "./components/about/about";
import {ExecutionStatusPipe} from "./pipes/execution-status-pipe";
import {TruncatePipe} from "./pipes/truncate-pipe";
import {ScenarioNamePipe} from "./pipes/scenario-name-pipe";
import {ActivityFilterPipe} from "./pipes/activity-filter-pipe";

@NgModule({
    imports: [
        BrowserModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        HeaderComponent,
        ScenarioExecutionList,
        ScenarioActionList,
        ScenarioParameterList,
        ScenarioParameterFormComponent,
        ScenarioParameterFormItemComponent,
        MessageListComponent,
        MessageHeaderList,
        AboutComponent,
        ExecutionStatusPipe,
        TruncatePipe,
        ScenarioNamePipe,
        ActivityFilterPipe,
        routedComponents
    ],
    providers: [
        AppInfoService,
        ActivityService,
        MessageService,
        SummaryService,
        ScenarioParameterService,
        ScenarioService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}

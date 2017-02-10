import {NgModule} from "@angular/core";
import {BrowserModule, Title} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import "../../rxjs-extensions";
import {AppComponent} from "./app.component";
import {NavbarComponent} from "../navbar/navbar";
import {AppRoutingModule, routedComponents} from "./app-routing.module";
import {AppInfoService} from "../../services/appinfo-service";
import {ConfigService} from "../../services/config-service";
import {ExecutionService} from "../../services/execution-service";
import {MessageService} from "../../services/message-service";
import {StatusService} from "../../services/status-service";
import {TestParameterControlService} from "../../services/test-parameter-control-service";
import {TestService} from "../../services/test-service";
import {TestExecutionList} from "../test-execution-list/test-execution-list";
import {TestActionList} from "../test-action-list/test-action-list";
import {TestParameterList} from "../test-parameter-list/test-parameter-list";
import {TestParameterFormComponent} from "../test-parameter-form/test-parameter-form";
import {TestParameterFormItemComponent} from "../test-parameter-form-item/test-parameter-form-item";
import {MessageListComponent} from "../message-list/message-list";
import {AboutComponent} from "../about/about";
import {HelpComponent} from "../help/help";
import {ExecutionStatusPipe} from "../../pipes/execution-status-pipe";
import {TruncatePipe} from "../../pipes/truncate-pipe";
import {ScenarioNamePipe} from "../../pipes/scenario-name-pipe";
import {ActivityFilterPipe} from "../../pipes/activity-filter-pipe";
import {MessageFilterPipe} from "../../pipes/message-filter-pipe";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        AppRoutingModule,
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
        routedComponents,
    ],
    providers: [
        Title,
        AppInfoService,
        ConfigService,
        ExecutionService,
        MessageService,
        StatusService,
        TestParameterControlService,
        TestService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}

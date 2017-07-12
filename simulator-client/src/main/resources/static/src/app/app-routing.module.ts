import {NgModule} from "@angular/core";
import {Routes, RouterModule} from "@angular/router";
import {WelcomeComponent} from "./components/welcome/welcome";
import {StatusComponent} from "./components/status/status";
import {ActivityComponent} from "./components/activity/activity";
import {TestExecutionDetailComponent} from "./components/test-execution-detail/test-execution-detail";
import {TestsComponent} from "./components/tests/tests";
import {TestDetailComponent} from "./components/test-detail/test-detail";
import {TestLaunchComponent} from "./components/test-launch/test-launch";
import {MessagesComponent} from "./components/messages/messages";
import {MessageDetailComponent} from "./components/message-detail/message-detail";
import {AboutComponent} from "./components/about/about";
import {HelpComponent} from "./components/help/help";
import {LocationStrategy, HashLocationStrategy} from "@angular/common";
import {environment} from "../environments/environment";

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: '/welcome'},
    {path: 'welcome', component: WelcomeComponent},
    {path: 'status', component: StatusComponent},
    {
        path: 'activity',
        children: [
            {path: '', component: ActivityComponent},
            {path: ':id', component: TestExecutionDetailComponent},
        ],
    },
    {
        path: 'tests',
        children: [
            {path: '', component: TestsComponent},
            {
                path: ':name',
                children: [
                    {path: 'detail', component: TestDetailComponent},
                    {path: 'launch', component: TestLaunchComponent},
                ]
            },
        ],
    },
    {
        path: 'messages',
        children: [
            {path: '', component: MessagesComponent},
            {path: ':id', component: MessageDetailComponent},
        ],
    },
    {path: 'help', component: HelpComponent},
    {path: 'about', component: AboutComponent}

];

@NgModule({
    imports: [
        RouterModule.forRoot(routes,  { enableTracing: environment.traceRouting })
    ],
    exports: [RouterModule],
    providers:    [
        {provide: LocationStrategy, useClass: HashLocationStrategy}
    ],
})
export class AppRoutingModule {
}

export const routedComponents = [
    WelcomeComponent,
    StatusComponent,
    ActivityComponent,
    TestExecutionDetailComponent,
    TestsComponent,
    TestDetailComponent,
    TestLaunchComponent,
    MessagesComponent,
    MessageDetailComponent
];

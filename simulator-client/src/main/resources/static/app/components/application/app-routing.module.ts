import {NgModule} from "@angular/core";
import {Routes, RouterModule} from "@angular/router";
import {WelcomeComponent} from "../welcome/welcome";
import {StatusComponent} from "../status/status";
import {ActivityComponent} from "../activity/activity";
import {TestExecutionDetailComponent} from "../test-execution-detail/test-execution-detail";
import {TestsComponent} from "../tests/tests";
import {TestDetailComponent} from "../test-detail/test-detail";
import {TestLaunchComponent} from "../test-launch/test-launch";
import {MessagesComponent} from "../messages/messages";
import {MessageDetailComponent} from "../message-detail/message-detail";
import {AboutComponent} from "../about/about";
import {HelpComponent} from "../help/help";
import {LocationStrategy, HashLocationStrategy} from "@angular/common";

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
    imports: [RouterModule.forRoot(routes)],
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
    MessageDetailComponent,
];

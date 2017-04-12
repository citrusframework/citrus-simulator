import {Component, OnInit} from "@angular/core";
import {AppInfoService} from "../../services/appinfo-service";
import {AppInfo} from "../../model/appinfo";

@Component({
    moduleId: module.id,
    selector: 'simulator-navbar',
    templateUrl: 'navbar.html',
    styleUrls: ['navbar.css'],
})
export class NavbarComponent implements OnInit {
    appInfo: AppInfo;

    constructor(private appInfoService: AppInfoService) {
        this.appInfoService.getAppInfo().subscribe(
            appInfo => this.appInfo = appInfo,
            error => console.log(error),
            () => console.log("Info has been retrieved: simulator name = '%s', domain = '%s', version = '%s'",
                this.appInfo.simulatorName,
                this.appInfo.simulatorDomain,
                this.appInfo.simulatorVersion));
    }

    ngOnInit(): void {
        this.appInfoService.getAppInfo();
    }
}

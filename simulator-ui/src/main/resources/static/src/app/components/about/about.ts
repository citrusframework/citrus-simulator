import {Component, OnInit} from "@angular/core";
import {AppInfoService} from "../../services/appinfo-service";
import {SimulatorInfo} from "../../model/manage";

@Component({
    moduleId: module.id,
    templateUrl: 'about.html'
})
export class AboutComponent implements OnInit {

    simulatorInfo: SimulatorInfo;

    constructor(private appInfoService: AppInfoService) {
        this.appInfoService.getAppInfo().subscribe(
            appInfo => this.simulatorInfo = appInfo.simulator,
            error => console.log(error),
            () => console.log("Info has been retrieved: simulator name = '%s', domain = '%s', version = '%s'",
                this.simulatorInfo.name,
                this.simulatorInfo.version));
    }

    ngOnInit(): void {
        this.appInfoService.getAppInfo();
    }

    // TODO CD include the environment properties in the about page (/api/manage/env)
}

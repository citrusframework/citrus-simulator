import {Component, OnInit} from "@angular/core";
import {AppInfoService} from "../../services/appinfo-service";
import {Simulator} from "../../model/simulator";

@Component({
    moduleId: module.id,
    templateUrl: 'about.html'
})
export class AboutComponent implements OnInit {
    
    simulator: Simulator;

    constructor(private appInfoService: AppInfoService) {
        this.appInfoService.getSimulatorInfo().subscribe(
            simulator => this.simulator = simulator,
            error => console.log(error),
            () => console.log("Info has been retrieved: simulator name = '%s', domain = '%s', version = '%s'",
                this.simulator.name,
                this.simulator.domain,
                this.simulator.version));
    }

    ngOnInit(): void {
        this.appInfoService.getSimulatorInfo();
    }

    // TODO CD include the environment properties in the about page (/api/manage/env)
}

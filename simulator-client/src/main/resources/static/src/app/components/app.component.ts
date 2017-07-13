import {Component} from "@angular/core";
import {Title} from "@angular/platform-browser";
import {AppInfoService} from "../services/appinfo-service";

@Component({
    selector: 'app',
    templateUrl: 'app.html',
})
export class AppComponent {
    constructor(private titleService: Title, private appInfoService: AppInfoService) {
        this.appInfoService.getSimulatorInfo().subscribe(
            simulator => this.titleService.setTitle(simulator.name),
            error => console.log(error));
    }
}

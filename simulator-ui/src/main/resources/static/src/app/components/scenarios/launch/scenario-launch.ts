import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {Scenario, ScenarioParameter} from "../../../model/scenario";
import {ScenarioService} from "../../../services/scenario-service";

@Component({
    moduleId: module.id,
    selector: 'scenario-launch',
    templateUrl: 'scenario-launch.html',
    providers: [ScenarioService]
})
export class ScenarioLaunchComponent implements OnInit {
    title = 'Scenario Launch';
    scenario: Scenario;
    scenarioParameters: ScenarioParameter[];
    errorMessage: string;

    constructor(
        private scenarioService: ScenarioService,
        private route: ActivatedRoute,
        private location: Location) {
    }

    ngOnInit() {
        let name = this.route.snapshot.params['name'];
        this.getScenario(name);
        this.getScenarioParameters(name);
    }

    getScenario(name: string) {
        this.scenarioService.getScenario(name)
            .subscribe(
                scenario => this.scenario = scenario[0],
                error => this.errorMessage = <any>error
            );
    }

    getScenarioParameters(name: string) {
        this.scenarioService.getScenarioParameters(name)
            .subscribe(
                scenarioParameters => this.scenarioParameters = scenarioParameters,
                error => this.errorMessage = <any>error
            );
    }

    goBack() {
        this.location.back();
        return false;
    }
}

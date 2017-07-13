import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {Scenario} from "../../model/scenario";
import {ScenarioService} from "../../services/scenario-service";
import {ActivityService} from "../../services/activity-service";

@Component({
    moduleId: module.id,
    templateUrl: 'scenario.html',
    styleUrls: ['scenario.css']
})
export class ScenarioComponent implements OnInit {
    title = 'Scenarios';
    inputValue: string = '';
    includeStarter: boolean = true;
    includeNonStarter: boolean = true;
    starterState: string = 'active';
    nonStarterState: string = 'active';

    scenarios: Scenario[];
    selectedScenario: Scenario;
    errorMessage: string;

    constructor(private router: Router,
                private activityService: ActivityService,
                private scenarioService: ScenarioService) {
    }

    ngOnInit() {
        this.getScenarios();
    }

    getScenarios() {
        this.scenarioService.getScenarios()
            .subscribe(
                scenarios => this.scenarios = scenarios,
                error => this.errorMessage = <any>error
            );
    }

    onSelect(scenario: Scenario) {
        this.selectedScenario = scenario;
        this.gotoScenarioDetails(scenario);
    }

    gotoScenarioDetails(scenario: Scenario) {
        this.router.navigate(['scenario', 'detail', scenario.name]);
    }

    launchScenario(scenario: Scenario) {
        this.scenarioService.getScenarioParameters(scenario.name)
            .subscribe(
                scenarioParameters => {
                    if (scenarioParameters.length > 0) {
                        this.gotoScenarioLaunch(scenario);
                    } else {
                        this.launchScenarioNow(scenario);
                    }
                },
                error => this.errorMessage = <any>error
            );
    }

    gotoScenarioLaunch(scenario: Scenario) {
        this.router.navigate(['scenario', 'launch', scenario.name]);
    }

    launchScenarioNow(scenario: Scenario) {
        this.scenarioService.launchScenario(scenario.name, []).subscribe(
            executionId => {
                this.router.navigate(['activity', executionId]);
            },
            error => this.errorMessage = <any>error
        );
    }

    toggleStarter() {
        this.includeStarter = !this.includeStarter;
        if (this.includeStarter) {
            this.starterState = 'active';
        } else {
            this.starterState = '';
        }
    }

    toggleNonStarter() {
        this.includeNonStarter = !this.includeNonStarter;
        if (this.includeNonStarter) {
            this.nonStarterState = 'active';
        } else {
            this.nonStarterState = '';
        }
    }
}

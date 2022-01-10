import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {Scenario} from "../../model/scenario";
import {ScenarioService} from "../../services/scenario-service";
import {ActivityService} from "../../services/activity-service";

@Component({
    moduleId: module.id,
    templateUrl: 'scenario.html',
    styleUrls: ['scenario.css',  '../../styles/filter-section.css']
})
export class ScenarioComponent implements OnInit {
    inputValue: string = '';
    starterState: boolean = true;
    nonStarterState: boolean = true

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
            .subscribe({
                next: (scenarios) => this.scenarios = scenarios,
                error: (error) => this.errorMessage = error.message
            });
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
            .subscribe({
                next: (scenarioParameters) => {
                    if (scenarioParameters.length > 0) {
                        this.gotoScenarioLaunch(scenario);
                    } else {
                        this.launchScenarioNow(scenario);
                    }
                },
                error: (error) => this.errorMessage = error.message
            });
    }

    gotoScenarioLaunch(scenario: Scenario) {
        this.router.navigate(['scenario', 'launch', scenario.name]);
    }

    launchScenarioNow(scenario: Scenario) {
        this.scenarioService.launchScenario(scenario.name, [])
            .subscribe({
                next: (executionId) => {
                    this.router.navigate(['activity', executionId]);
                },
                error: (error) => this.errorMessage = error.message
            });
    }

    toggleStarter() {
        this.starterState = !this.starterState
    }

    toggleNonStarter() {
        this.nonStarterState = !this.nonStarterState;
    }
}

import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {Scenario, ScenarioExecution, ScenarioParameter} from "../../../model/scenario";
import {ScenarioService} from "../../../services/scenario-service";
import {ActivityService} from "../../../services/activity-service";
import {ScenarioExecutionFilter} from "../../../model/filter";

@Component({
    moduleId: module.id,
    selector: 'scenario-detail',
    templateUrl: 'scenario-detail.html',
    styleUrls: ['scenario-detail.css',  '../../../styles/filter-section.css'],
    providers: [ScenarioService, ActivityService]
})
export class ScenarioDetailComponent implements OnInit {
    
    scenario: Scenario;
    scenarioParameters: ScenarioParameter[];
    scenarioExecutions: ScenarioExecution[];
    errorMessage: string;

    successState: boolean = true;
    failedState: boolean = true;
    activeState: boolean = true;

    scenarioExecutionFilter: ScenarioExecutionFilter;

    constructor(private scenarioService: ScenarioService,
                private activityService: ActivityService,
                private route: ActivatedRoute,
                private router: Router,
                private location: Location) {
    }

    ngOnInit() {
        let name = this.route.snapshot.params['name'];
        this.scenarioExecutionFilter = this.initScenarioExecutionFilter(name);
        this.getScenario(this.scenarioExecutionFilter.scenarioName);
        this.getScenarioExecutions(this.scenarioExecutionFilter.scenarioName);
    }

    getScenario(name: string) {
        this.scenarioService.getScenario(name)
            .subscribe({
                next: (scenario) => {
                    // TODO MM fix - should not have multiple scenario matching same name
                    this.scenario = scenario[0];
                    if(this.scenario.type == 'STARTER') {
                        this.getScenarioParameters(name);
                    }
                },
                error: (error) => this.errorMessage = error.toString()
            });
    }

    getScenarioParameters(name: string) {
        this.scenarioService.getScenarioParameters(name)
            .subscribe({
                next: (scenarioParameters) => this.scenarioParameters = scenarioParameters,
                error: (error) => this.errorMessage = error.toString()
            });
    }

    getScenarioExecutions(name: string) {
        this.includeStatusInRequest();
        this.activityService.getScenarioExecutions(this.scenarioExecutionFilter)
            .subscribe({
                next: (scenarioExecutions) => this.scenarioExecutions = scenarioExecutions,
                error: (error) => this.errorMessage = error.toString()
            });
    }

    launchScenario() {
        if (this.scenarioParameters.length > 0) {
            this.gotoScenarioLaunch();
        } else {
            this.launchScenarioNow();
        }
    }

    gotoScenarioLaunch() {
        this.router.navigate(['scenario', 'launch', this.scenario.name]);
    }

    launchScenarioNow() {
        this.scenarioService.launchScenario(this.scenario.name, this.scenarioParameters)
            .subscribe({
                next: (executionId) => {
                    this.router.navigate(['activity', executionId]);
                },
                error: (error) => this.errorMessage = error.toString()
            });
    }

    includeStatusInRequest() {
        this.scenarioExecutionFilter.executionStatus = [ (this.successState) ? "SUCCESS" : undefined,
            (this.failedState) ? "FAILED" : undefined, (this.activeState) ? "ACTIVE" : undefined];
    }

    goBack() {
        this.location.back();
        return false;
    }

    toggleSuccess() {
        this.successState = !this.successState;
    }

    toggleFailed() {
        this.failedState = !this.failedState;
    }

    toggleActive() {
        this.activeState = !this.activeState;
    }

    initScenarioExecutionFilter(name: string): ScenarioExecutionFilter {
        return new ScenarioExecutionFilter(null, null, null, null, null, name, []);
    }
}

import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {Scenario, ScenarioExecution, ScenarioParameter} from "../../../model/scenario";
import {ScenarioService} from "../../../services/scenario-service";
import {ExecutionService} from "../../../services/execution-service";

@Component({
    moduleId: module.id,
    selector: 'scenario-detail',
    templateUrl: 'scenario-detail.html',
    styleUrls: ['scenario-detail.css'],
    providers: [ScenarioService, ExecutionService]
})
export class ScenarioDetailComponent implements OnInit {
    title = 'Scenario';
    scenario: Scenario;
    scenarioParameters: ScenarioParameter[];
    scenarioExecutions: ScenarioExecution[];
    errorMessage: string;

    inputValue: string = '';
    displayActivityFilter: boolean = false;
    includeSuccess: boolean = true;
    includeFailed: boolean = true;
    includeActive: boolean = true;
    successState: string = 'active';
    failedState: string = 'active';
    activeState: string = 'active';

    constructor(private scenarioService: ScenarioService,
                private executionService: ExecutionService,
                private route: ActivatedRoute,
                private router: Router,
                private location: Location) {
    }

    ngOnInit() {
        let name = this.route.snapshot.params['name'];
        this.getScenario(name);
        this.getScenarioExecutions(name);
    }

    getScenario(name: string) {
        this.scenarioService.getScenario(name)
            .subscribe(
                scenario => {
                    this.scenario = scenario;
                    if(scenario.type == 'STARTER') {
                        this.getScenarioParameters(name);
                    }
                },
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


    getScenarioExecutions(name: string) {
        this.executionService.getScenarioExecutionsByScenarioName(name)
            .subscribe(
                scenarioExecutions => this.scenarioExecutions = scenarioExecutions,
                error => this.errorMessage = <any>error
            );
    }

    launchScenario() {
        if(this.scenarioParameters.length > 0) {
            this.gotoScenarioLaunch();
        } else {
            this.launchScenarioNow();
        }
    }

    gotoScenarioLaunch() {
        this.router.navigate(['scenario', 'launch', this.scenario.name]);
    }

    launchScenarioNow() {
        this.scenarioService.launchScenario(this.scenario.name, this.scenarioParameters).subscribe(
            executionId => {
                this.router.navigate(['activity', executionId]);
            },
            error => this.errorMessage = <any>error
        );
    }

    goBack() {
        this.location.back();
        return false;
    }

    toggleSuccess() {
        this.includeSuccess = !this.includeSuccess;
        if(this.includeSuccess) {
            this.successState = 'active';
        } else {
            this.successState = 'deactivated';
        }
    }

    toggleFailed() {
        this.includeFailed = !this.includeFailed;
        if(this.includeFailed) {
            this.failedState = 'active';
        } else {
            this.failedState = 'deactivated';
        }
    }

    toggleActive() {
        this.includeActive = !this.includeActive;
        if(this.includeActive) {
            this.activeState = 'active';
        } else {
            this.activeState = 'deactivated';
        }
    }

    toggleActivityFilterDisplay() {
        this.displayActivityFilter = !this.displayActivityFilter;
    }

}

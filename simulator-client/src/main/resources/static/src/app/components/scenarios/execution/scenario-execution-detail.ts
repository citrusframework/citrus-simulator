import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {ScenarioExecution} from "../../../model/scenario";
import {ExecutionService} from "../../../services/execution-service";

@Component({
    moduleId: module.id,
    selector: 'scenario-execution-detail',
    templateUrl: 'scenario-execution-detail.html',
    providers: [ExecutionService]
})
export class ScenarioExecutionDetailComponent implements OnInit {
    title = 'Scenario Execution';
    scenarioExecution: ScenarioExecution;
    format: string = "yyyy-MM-dd HH:mm:ss";
    errorMessage: string;

    constructor(
        private executionService: ExecutionService,
        private route: ActivatedRoute,
        private location: Location) {
    }

    ngOnInit() {
        let id = +this.route.snapshot.params['id'];
        this.getScenarioExecutionsById(id);
    }

    getScenarioExecutionsById(id: number) {
        this.executionService.getScenarioExecutionById(id)
            .subscribe(
                scenarioExecution => this.scenarioExecution = scenarioExecution,
                error => this.errorMessage = <any>error
            );
    }

    goBack() {
        this.location.back();
        return false;
    }
}

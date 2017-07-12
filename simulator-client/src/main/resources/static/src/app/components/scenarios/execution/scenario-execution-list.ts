import {Component, Input} from "@angular/core";
import {Router} from "@angular/router";
import {ScenarioExecution} from "../../../model/scenario";

@Component({
    moduleId: module.id,
    selector: 'scenario-execution-list',
    templateUrl: 'scenario-execution-list.html',
    styleUrls: ['scenario-execution-list.css'],
})
export class ScenarioExecutionList {
    @Input() hideScenarioname: boolean;
    @Input() hideStatus: boolean;
    @Input() scenarioExecutions: ScenarioExecution[];

    selectedScenarioExecution: ScenarioExecution;
    format: string = "yyyy-MM-dd HH:mm:ss";

    constructor(private router: Router) {
    }

    onSelect(scenarioExecution: ScenarioExecution) {
        this.selectedScenarioExecution = scenarioExecution;
        this.gotoScenarioExecutionDetail();
    }

    gotoScenarioExecutionDetail() {
        this.router.navigate(['activity', this.selectedScenarioExecution.executionId]);
    }

}

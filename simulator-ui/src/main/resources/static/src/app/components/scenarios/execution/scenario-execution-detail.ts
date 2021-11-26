import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {ScenarioExecution} from "../../../model/scenario";
import {ActivityService} from "../../../services/activity-service";

@Component({
    moduleId: module.id,
    selector: 'scenario-execution-detail',
    templateUrl: 'scenario-execution-detail.html',
    providers: [ActivityService]
})
export class ScenarioExecutionDetailComponent implements OnInit {
    title = 'Scenario Execution';
    scenarioExecution: ScenarioExecution;
    format: string = "yyyy-MM-dd HH:mm:ss";
    errorMessage: string;

    constructor(
        private activityService: ActivityService,
        private route: ActivatedRoute,
        private location: Location) {
    }

    ngOnInit() {
        let id = +this.route.snapshot.params['id'];
        this.getScenarioExecutionsById(id);
    }

    getScenarioExecutionsById(id: number) {
        this.activityService.getScenarioExecutionById(id)
            .subscribe({
                next: (scenarioExecution) => this.scenarioExecution = scenarioExecution,
                error: (error) => this.errorMessage = <any>error
            });
    }

    goBack() {
        this.location.back();
        return false;
    }
}

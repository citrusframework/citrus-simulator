import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {SummaryService} from '../../services/summary-service';
import {Summary, ScenarioExecution} from '../../model/scenario';
import {ActivityService} from '../../services/activity-service';

@Component({
    moduleId: module.id,
    selector: 'simulator-status-page',
    templateUrl: 'status.html',
    styleUrls: ['status.css'],
})
export class StatusComponent implements OnInit {
    title = 'Status';
    summary: Summary;
    active: number;
    scenarioExecutions: ScenarioExecution[];
    errorMessage: string;

    constructor(
        private router: Router,
        private summaryService: SummaryService,
        private activityService: ActivityService) {
    }

    ngOnInit() {
        this.getSummary();
        this.getActive();
    }

    getSummary() {
        this.summaryService.getSummary()
            .subscribe(
                summary => this.summary = summary,
                error => this.errorMessage = <any>error
            );
    }

    getActive() {
        this.summaryService.getCountActiveScenarios()
            .subscribe(
                active => this.active = active,
                error => this.errorMessage = <any>error
            );
    }

    onSelect(status: string) {
        this.router.navigate(['activity', { status: status}]);
    }

    clearStatusInformation() {
        this.summaryService.resetSummary()
            .subscribe(
                summary => this.summary = summary,
                error => this.errorMessage = <any>error
            );
    }

    refreshStatusInformation() {
        this.getSummary();
    }

}

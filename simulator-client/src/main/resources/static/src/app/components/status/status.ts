import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {StatusService} from '../../services/status-service';
import {Summary, ScenarioExecution} from '../../model/scenario';
import {ExecutionService} from '../../services/execution-service';

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
        private statusService: StatusService,
        private executionService: ExecutionService) {
    }

    ngOnInit() {
        this.getSummary();
        this.getActive();
    }

    getSummary() {
        this.statusService.getSummary()
            .subscribe(
                summary => this.summary = summary,
                error => this.errorMessage = <any>error
            );
    }

    getActive() {
        this.statusService.getCountActiveScenarios()
            .subscribe(
                active => this.active = active,
                error => this.errorMessage = <any>error
            );
    }

    onSelect(status: string) {
        this.router.navigate(['activity', { status: status}]);
    }

    clearStatusInformation() {
        this.statusService.resetSummary()
            .subscribe(
                summary => this.summary = summary,
                error => this.errorMessage = <any>error
            );
    }

    refreshStatusInformation() {
        this.getSummary();
    }

}

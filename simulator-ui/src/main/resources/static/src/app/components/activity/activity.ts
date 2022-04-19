import {Component, OnInit, AfterViewInit, OnDestroy, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {ActivityService} from "../../services/activity-service";
import {ScenarioExecution} from "../../model/scenario";
import {ScenarioExecutionFilter} from "../../model/filter";
import {MatInput} from "@angular/material/input";
import {convertTime} from "../shared/date-time";

@Component({
    moduleId: module.id,
    templateUrl: 'activity.html',
    styleUrls: ['activity.css', '../../styles/filter-section.css'],
    selector: "app-root",
})
export class ActivityComponent implements OnInit, OnDestroy, AfterViewInit {
    scenarioExecutionFilter: ScenarioExecutionFilter;
    scenarioExecutions: ScenarioExecution[];
    errorMessage: string;

    inputIncludeFilterInRequest: boolean = false;
    autoRefreshId: number;

    successState: boolean = true;
    failedState: boolean = true;
    activeState: boolean = true;

    inputDateFrom: any;
    inputTimeFrom: any;
    inputDateTo: any;
    inputTimeTo: any;

    constructor(
        private activityService: ActivityService,
        private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.scenarioExecutionFilter = this.initScenarioExecutionFilter();
        this.getActivities();
        let statusFilter = this.route.snapshot.params['status'];
        if(statusFilter) {
            if (statusFilter.toLowerCase().indexOf("success") > -1) {
                this.toggleFailed();
                this.toggleActive();
            } else if(statusFilter.toLowerCase().indexOf("failed") > -1) {
                this.toggleSuccess();
                this.toggleActive();
            } else if(statusFilter.toLowerCase().indexOf("active") > -1) {
                this.toggleSuccess();
                this.toggleFailed();
            }
        }

        this.autoRefreshId = window.setInterval(() => { if (this.scenarioExecutionFilter.pageNumber == 0 && this.scenarioExecutionFilter.pageSize < 250) {
            this.getActivities();
        } }, 2000);
    }

    ngOnDestroy(): void {
        window.clearInterval(this.autoRefreshId);
    }

    getActivities() {
        this.includeStatusInRequest();
        this.activityService.getScenarioExecutions(this.scenarioExecutionFilter)
            .subscribe( {
                next: (scenarioExecutions) => this.scenarioExecutions = scenarioExecutions,
                error: (error) => this.errorMessage = error.toString()
            });
    }

    clearActivities() {
        this.activityService.clearScenarioExecutions().subscribe({
            next: (success) => this.getActivities(),
            error: (error) => this.errorMessage = error.toString()
        });
    }

    includeStatusInRequest() {
        this.scenarioExecutionFilter.executionStatus = [ (this.successState) ? "SUCCESS" : undefined,
            (this.failedState) ? "FAILED" : undefined, (this.activeState) ? "ACTIVE" : undefined];
    }

    prev() {
        if (this.scenarioExecutionFilter.pageNumber > 0) {
            this.scenarioExecutionFilter.pageNumber--;
            this.getActivities();
        }
    }

    next() {
        if (this.scenarioExecutions && this.scenarioExecutions.length) {
            this.scenarioExecutionFilter.pageNumber++;
            this.getActivities();
        }
    }

    setDateTimeFrom(): void {
        if (this.inputDateFrom && this.inputTimeFrom) {
            this.scenarioExecutionFilter.fromDate = convertTime(this.inputDateFrom, this.inputTimeFrom);
        } else {
            this.scenarioExecutionFilter.fromDate = null;
            this.getActivities();
        }
    }

    setDateTimeTo(): void {
        if (this.inputDateTo && this.inputTimeTo) {
            this.scenarioExecutionFilter.toDate = convertTime(this.inputDateTo, this.inputTimeTo);
        } else {
            this.scenarioExecutionFilter.toDate = null;
            this.getActivities();
        }
    }

    toggleSuccess() {
        this.successState = !this.successState;
        this.getActivities();
    }

    toggleFailed() {
        this.failedState = !this.failedState;
        this.getActivities();
    }

    toggleActive() {
        this.activeState = !this.activeState;
        this.getActivities();
    }

    initScenarioExecutionFilter(): ScenarioExecutionFilter {
        return new ScenarioExecutionFilter(null, null, 0, 25, '', '', [] );
    }

    ngAfterViewInit(): void {
    }

    /* used for clearing the values in the date fields */
    @ViewChild('dateFromInput', {read: MatInput}) dateFromInput: MatInput;
    @ViewChild('dateToInput', {read: MatInput}) dateToInput: MatInput;

    resetDateFrom() {
        this.inputDateFrom = null;
        this.dateFromInput.value = null;
        this.setDateTimeFrom();
    }

    resetTimeFrom() {
        this.inputTimeFrom = null;
        this.setDateTimeFrom();
    }

    resetDateTo() {
        this.inputDateTo = null;
        this.dateToInput.value = null;
        this.setDateTimeTo();
    }

    resetTimeTo() {
        this.inputTimeTo = null;
        this.setDateTimeTo();
    }
}

import {Component, OnInit, AfterViewInit, OnDestroy} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {ActivityService} from "../../services/activity-service";
import {ScenarioExecution} from "../../model/scenario";

@Component({
    moduleId: module.id,
    templateUrl: 'activity.html',
    styleUrls: ['activity.css'],
})
export class ActivityComponent implements OnInit, OnDestroy, AfterViewInit {
    scenarioExecutions: ScenarioExecution[];
    errorMessage: string;

    inputValue: string = '';
    includeSuccess: boolean = true;
    includeFailed: boolean = true;
    includeActive: boolean = true;
    successState: string = 'active';
    failedState: string = 'active';
    activeState: string = 'active';

    pageSize = 25;
    page = 0;
    autoRefreshId: number;

    constructor(
        private activityService: ActivityService,
        private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.getActivity();
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

        this.autoRefreshId = window.setInterval(() => { if (this.page == 0 && this.pageSize < 250) {
            this.getActivity();
        } }, 2000);
    }

    ngOnDestroy(): void {
        window.clearInterval(this.autoRefreshId);
    }

    ngAfterViewInit(): void {
    }

    getActivity() {
        this.activityService.getScenarioExecutions(this.page, this.pageSize)
            .subscribe({
                next: (scenarioExecutions) => this.scenarioExecutions = scenarioExecutions,
                error: (error) => this.errorMessage = <any>error
            });
    }

    clearActivity() {
        this.activityService.clearScenarioExecutions()
            .subscribe({
                next: (success) => this.getActivity(),
                error: (error) => this.errorMessage = <any>error
            });
    }

    prev() {
        if (this.page > 0) {
            this.page--;
            this.getActivity();
        }
    }

    next() {
        if (this.scenarioExecutions && this.scenarioExecutions.length) {
            this.page++;
            this.getActivity();
        }
    }

    toggleSuccess() {
        this.includeSuccess = !this.includeSuccess;
        if(this.includeSuccess) {
            this.successState = 'active';
        } else {
            this.successState = '';
        }
    }

    toggleFailed() {
        this.includeFailed = !this.includeFailed;
        if(this.includeFailed) {
            this.failedState = 'active';
        } else {
            this.failedState = '';
        }
    }

    toggleActive() {
        this.includeActive = !this.includeActive;
        if(this.includeActive) {
            this.activeState = 'active';
        } else {
            this.activeState = '';
        }
    }
}

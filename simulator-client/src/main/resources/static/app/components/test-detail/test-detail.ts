import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {Test, TestExecution, TestParameter} from "../../model/test";
import {TestService} from "../../services/test-service";
import {ExecutionService} from "../../services/execution-service";

@Component({
    moduleId: module.id,
    selector: 'test-detail',
    templateUrl: 'test-detail.html',
    styleUrls: ['test-detail.css'],
    providers: [TestService, ExecutionService]
})
export class TestDetailComponent implements OnInit {
    title = 'Scenario';
    test: Test;
    testParameters: TestParameter[];
    testExecutions: TestExecution[];
    errorMessage: string;

    inputValue: string = '';
    displayActivityFilter: boolean = false;
    includeSuccess: boolean = true;
    includeFailed: boolean = true;
    includeActive: boolean = true;
    successState: string = 'active';
    failedState: string = 'active';
    activeState: string = 'active';

    constructor(private testService: TestService,
                private executionService: ExecutionService,
                private route: ActivatedRoute,
                private router: Router,
                private location: Location) {
    }

    ngOnInit() {
        let name = this.route.snapshot.params['name'];
        this.getTest(name);
        this.getTestExecutions(name);
    }

    getTest(name: string) {
        this.testService.getTest(name)
            .subscribe(
                test => {
                    this.test = test;
                    if(test.type == 'STARTER') {
                        this.getTestParameters(name);
                    }
                },
                error => this.errorMessage = <any>error
            );
    }

    getTestParameters(name: string) {
        this.testService.getTestParameters(name)
            .subscribe(
                testParameters => this.testParameters = testParameters,
                error => this.errorMessage = <any>error
            );
    }


    getTestExecutions(name: string) {
        this.executionService.getTestExecutionsByTestName(name)
            .subscribe(
                testExecutions => this.testExecutions = testExecutions,
                error => this.errorMessage = <any>error
            );
    }

    launchTest() {
        if(this.testParameters.length > 0) {
            this.gotoTestLaunch();
        } else {
            this.launchTestNow();
        }
    }

    gotoTestLaunch() {
        this.router.navigate(['/tests', this.test.name, 'launch']);
    }

    launchTestNow() {
        this.testService.launchTest(this.test.name, this.testParameters).subscribe(
            executionId => {
                this.router.navigate(['/activity', executionId]);
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

import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {Test} from "../../model/test";
import {TestService} from "../../services/test-service";
import {ExecutionService} from "../../services/execution-service";

@Component({
    moduleId: module.id,
    selector: 'simulator-tests-page',
    templateUrl: 'tests.html',
    styleUrls: ['tests.css']
})
export class TestsComponent implements OnInit {
    title = 'Scenarios';
    inputValue: string = '';
    displayFilter: boolean = false;
    includeStarter: boolean = true;
    includeNonStarter: boolean = true;
    starterState: string = 'active';
    nonStarterState: string = 'active';

    tests: Test[];
    selectedTest: Test;
    errorMessage: string;

    constructor(private router: Router,
                private executionService: ExecutionService,
                private testService: TestService) {
    }

    ngOnInit() {
        this.getTests();
    }

    getTests() {
        this.testService.getTests()
            .subscribe(
                tests => this.tests = tests,
                error => this.errorMessage = <any>error
            );
    }

    onSelect(test: Test) {
        this.selectedTest = test;
        this.gotoScenarioDetails(test);
    }

    gotoScenarioDetails(test: Test) {
        this.router.navigate(['tests', test.name, 'detail']);
    }

    launchScenario(test: Test) {
        this.testService.getTestParameters(test.name)
            .subscribe(
                testParameters => {
                    if (testParameters.length > 0) {
                        this.gotoScenarioLaunch(test);
                    } else {
                        this.launchScenarioNow(test);
                    }
                },
                error => this.errorMessage = <any>error
            );
    }

    gotoScenarioLaunch(test: Test) {
        this.router.navigate(['/tests', test.name, 'launch']);
    }

    launchScenarioNow(test: Test) {
        this.testService.launchTest(test.name, []).subscribe(
            executionId => {
                this.router.navigate(['/activity', executionId]);
            },
            error => this.errorMessage = <any>error
        );
    }

    toggleFilterDisplay() {
        this.displayFilter = !this.displayFilter;
    }

    toggleStarter() {
        this.includeStarter = !this.includeStarter;
        if (this.includeStarter) {
            this.starterState = 'active';
        } else {
            this.starterState = '';
        }
    }

    toggleNonStarter() {
        this.includeNonStarter = !this.includeNonStarter;
        if (this.includeNonStarter) {
            this.nonStarterState = 'active';
        } else {
            this.nonStarterState = '';
        }
    }
}

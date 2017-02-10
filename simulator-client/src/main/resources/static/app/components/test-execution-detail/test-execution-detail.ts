import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {TestExecution} from "../../model/test";
import {ExecutionService} from "../../services/execution-service";

@Component({
    moduleId: module.id,
    selector: 'test-execution-detail',
    templateUrl: 'test-execution-detail.html',
    providers: [ExecutionService]
})
export class TestExecutionDetailComponent implements OnInit {
    title = 'Scenario Execution';
    testExecution: TestExecution;
    format: string = "yyyy-MM-dd HH:mm:ss";
    errorMessage: string;

    constructor(
        private executionService: ExecutionService,
        private route: ActivatedRoute,
        private location: Location) {
    }

    ngOnInit() {
        let id = +this.route.snapshot.params['id'];
        this.getTestExecutionsById(id);
    }

    getTestExecutionsById(id: number) {
        this.executionService.getTestExecutionById(id)
            .subscribe(
                testExecution => this.testExecution = testExecution,
                error => this.errorMessage = <any>error
            );
    }

    goBack() {
        this.location.back();
        return false;
    }
}

import {Component, Input} from "@angular/core";
import {Router} from "@angular/router";
import {TestExecution} from "../../model/test";

@Component({
    moduleId: module.id,
    selector: 'test-execution-list',
    templateUrl: 'test-execution-list.html',
    styleUrls: ['test-execution-list.css'],
})
export class TestExecutionList {
    @Input() hideTestname: boolean;
    @Input() hideStatus: boolean;
    @Input() testExecutions: TestExecution[];

    selectedTestExecution: TestExecution;
    format: string = "yyyy-MM-dd HH:mm:ss";

    constructor(private router: Router) {
    }

    onSelect(testExecution: TestExecution) {
        this.selectedTestExecution = testExecution;
        this.gotoTestExecutionDetail();
    }

    gotoTestExecutionDetail() {
        this.router.navigate(['activity', this.selectedTestExecution.executionId]);
    }

}

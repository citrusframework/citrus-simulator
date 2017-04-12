import {Component, Input, OnInit, OnChanges, SimpleChange} from "@angular/core";
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {FormGroup} from "@angular/forms";
import {TestParameter, Test} from "../../model/test";
import {TestService} from "../../services/test-service";
import {TestParameterControlService} from "../../services/test-parameter-control-service";

@Component({
    moduleId: module.id,
    selector: 'test-parameter-form',
    templateUrl: 'test-parameter-form.html',
    providers: [TestParameterControlService, TestService]
})
export class TestParameterFormComponent implements OnChanges, OnInit {
    @Input() test: Test;
    @Input() testParameters: TestParameter[] = [];
    form: FormGroup;
    errorMessage: string;

    constructor(
        private testParameterControlService: TestParameterControlService,
        private testService: TestService,
        private router: Router,
        private location: Location) {
    }

    ngOnInit() {
        this.renderForm();
    }

    ngOnChanges(changes: {[propName: string]: SimpleChange}) {
        this.renderForm();
    }

    onSubmit() {
        this.testParameters.forEach(testParameter => this.updateValue(testParameter));
        this.testService.launchTest(this.test.name, this.testParameters).subscribe(
            executionId => {
                this.router.navigate(['/activity', executionId]);
            },
            error => this.errorMessage = <any>error
        );
    }

    private renderForm() {
        this.form = this.testParameterControlService.toFormGroup(this.testParameters)
    }

    private updateValue(testParameter: TestParameter) {
        testParameter.value = this.form.controls[testParameter.name].value;
    }

    goBack() {
        this.location.back();
        return false;
    }
}

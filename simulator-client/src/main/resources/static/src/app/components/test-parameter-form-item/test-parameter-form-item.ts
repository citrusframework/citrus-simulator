import {Component, Input} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {TestParameter} from "../../model/test";

@Component({
    moduleId: module.id,
    selector: 'test-parameter-form-item',
    templateUrl: 'test-parameter-form-item.html',
    styleUrls: ['test-parameter-form-item.css']
})
export class TestParameterFormItemComponent {
    @Input() testParameter: TestParameter;
    @Input() form: FormGroup;

    get isValid() {
        if (this.testParameter.name && this.form.controls[this.testParameter.name]) {
            return this.form.controls[this.testParameter.name].valid;
        }
        return true;
    }
}

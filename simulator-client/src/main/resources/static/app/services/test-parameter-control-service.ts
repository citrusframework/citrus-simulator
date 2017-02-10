import {Injectable} from "@angular/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TestParameter} from "../model/test";

@Injectable()
export class TestParameterControlService {
    constructor() {
    }

    toFormGroup(testParameters: TestParameter[]) {
        let group: any = {};
        if (testParameters) {
            testParameters.forEach(testParameter => {
                group[testParameter.name] = testParameter.required
                    ? new FormControl(testParameter.value || '', Validators.required)
                    : new FormControl(testParameter.value || '');
            });
        }
        return new FormGroup(group);
    }
}

import {Injectable} from "@angular/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ScenarioParameter} from "../model/scenario";

@Injectable()
export class ScenarioParameterService {
    constructor() {
    }

    toFormGroup(scenarioParameters: ScenarioParameter[]) {
        let group: any = {};
        if (scenarioParameters) {
            scenarioParameters.forEach(scenarioParameter => {
                group[scenarioParameter.name] = scenarioParameter.required
                    ? new FormControl(scenarioParameter.value || '', Validators.required)
                    : new FormControl(scenarioParameter.value || '');
            });
        }
        return new FormGroup(group);
    }
}

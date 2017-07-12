import {Component, Input} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {ScenarioParameter} from "../../../model/scenario";

@Component({
    moduleId: module.id,
    selector: 'scenario-parameter-form-item',
    templateUrl: 'scenario-parameter-form-item.html',
    styleUrls: ['scenario-parameter-form-item.css']
})
export class ScenarioParameterFormItemComponent {
    @Input() scenarioParameter: ScenarioParameter;
    @Input() form: FormGroup;

    get isValid() {
        if (this.scenarioParameter.name && this.form.controls[this.scenarioParameter.name]) {
            return this.form.controls[this.scenarioParameter.name].valid;
        }
        return true;
    }
}

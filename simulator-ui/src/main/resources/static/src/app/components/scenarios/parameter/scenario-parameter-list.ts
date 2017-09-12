import {Component, Input} from "@angular/core";
import {ScenarioParameter} from "../../../model/scenario";

@Component({
    moduleId: module.id,
    selector:'scenario-parameter-list',
    templateUrl:'scenario-parameter-list.html',
    styleUrls:['scenario-parameter-list.css'],
})
export class ScenarioParameterList {
    @Input() scenarioParameters: ScenarioParameter[];
}

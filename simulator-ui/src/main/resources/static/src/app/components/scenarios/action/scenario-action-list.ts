import {Component, Input} from "@angular/core";
import {ScenarioAction} from "../../../model/scenario";

@Component({
    moduleId: module.id,
    selector: 'scenario-action-list',
    templateUrl: 'scenario-action-list.html',
    styleUrls:['scenario-action-list.css'],
})
export class ScenarioActionList {
    @Input() scenarioActions: ScenarioAction[];
    format: string = "yyyy-MM-dd HH:mm:ss";
}

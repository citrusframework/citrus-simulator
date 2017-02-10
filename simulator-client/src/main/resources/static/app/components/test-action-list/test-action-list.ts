import {Component, Input} from "@angular/core";
import {TestAction} from "../../model/test";

@Component({
    moduleId: module.id,
    selector: 'test-action-list',
    templateUrl: 'test-action-list.html',
    styleUrls:['test-action-list.css'],
})
export class TestActionList {
    @Input() testActions: TestAction[];
    format: string = "yyyy-MM-dd HH:mm:ss";
}

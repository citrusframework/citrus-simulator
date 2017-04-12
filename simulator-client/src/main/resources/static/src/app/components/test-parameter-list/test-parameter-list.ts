import {Component, Input} from "@angular/core";
import {TestParameter} from "../../model/test";

@Component({
    moduleId: module.id,
    selector:'test-parameter-list',
    templateUrl:'test-parameter-list.html',
    styleUrls:['test-parameter-list.css'],
})
export class TestParameterList {
    @Input() testParameters: TestParameter[];
}

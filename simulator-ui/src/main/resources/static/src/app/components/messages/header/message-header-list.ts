import {Component, Input} from "@angular/core";
import {MessageHeader} from "../../../model/scenario";

@Component({
    moduleId: module.id,
    selector: 'message-header-list',
    templateUrl: 'message-header-list.html',
    styleUrls:['message-header-list.css'],
})
export class MessageHeaderList {
    @Input() messageHeaders: MessageHeader[];
}

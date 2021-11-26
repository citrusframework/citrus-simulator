import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {Message} from "../../../model/scenario";
import {MessageService} from "../../../services/message-service";

@Component({
    moduleId: module.id,
    selector: 'message-detail',
    templateUrl: 'message-detail.html',
    styleUrls: ['message-detail.css'],
})
export class MessageDetailComponent implements OnInit {
    title = 'Message Details';
    message: Message;
    format: string = "yyyy-MM-dd HH:mm:ss";
    errorMessage: string;

    constructor(
        private messageService: MessageService,
        private route: ActivatedRoute,
        private location: Location) {
    }

    ngOnInit() {
        let id = +this.route.snapshot.params['id'];
        this.getMessageById(id);
    }

    getMessageById(id: number) {
        this.messageService.getMessageById(id)
            .subscribe({
                next: (message) => this.message = message,
                error: (error) => this.errorMessage = <any>error
            });
    }

    goBack() {
        this.location.back();
        return false;
    }
}

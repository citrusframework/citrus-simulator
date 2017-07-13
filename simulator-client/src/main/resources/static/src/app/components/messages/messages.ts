import {Component} from "@angular/core";
import {MessageService} from "../../services/message-service";
import {Message} from "../../model/scenario";

@Component({
    moduleId: module.id,
    templateUrl: 'messages.html',
    styleUrls: ['messages.css']

})
export class MessagesComponent {
    title = 'Messages';
    messages: Message[];
    errorMessage: string;

    inputValue: string = '';
    displayFilter: boolean = false;
    includeInbound: boolean = true;
    includeOutbound: boolean = true;
    inboundState: string = 'active';
    outboundState: string = 'active';

    constructor(private messageService: MessageService) {
    }

    ngOnInit() {
        this.getMessages();
    }

    getMessages() {
        this.messageService.getMessages()
            .subscribe(
                messages => this.messages = messages,
                error => this.errorMessage = <any>error
            );
    }

    toggleFilterDisplay() {
        this.displayFilter = !this.displayFilter;
    }

    clearAllMessages() {
        this.messageService.clearMessages().subscribe(
            success => this.getMessages(),
            error => this.errorMessage = <any>error
        );
    }

    toggleInbound() {
        this.includeInbound = !this.includeInbound;
        if(this.includeInbound) {
            this.inboundState = 'active';
        } else {
            this.inboundState = '';
        }
    }

    toggleOutbound() {
        this.includeOutbound = !this.includeOutbound;
        if(this.includeOutbound) {
            this.outboundState = 'active';
        } else {
            this.outboundState = '';
        }
    }
}

import {Component, OnInit, OnDestroy} from "@angular/core";
import {MessageService} from "../../services/message-service";
import {Message} from "../../model/scenario";

@Component({
    moduleId: module.id,
    templateUrl: 'messages.html',
    styleUrls: ['messages.css']

})
export class MessagesComponent implements OnInit, OnDestroy {
    messages: Message[];
    errorMessage: string;

    inputValue: string = '';
    includeInbound: boolean = true;
    includeOutbound: boolean = true;
    inboundState: string = 'active';
    outboundState: string = 'active';

    pageSize = 25;
    page = 0;
    autoRefreshId: number;

    constructor(private messageService: MessageService) {
    }

    ngOnInit() {
        this.getMessages();
        this.autoRefreshId = window.setInterval(() => { if (this.page == 0 && this.pageSize < 250) {
            this.getMessages();
        } }, 2000);
    }

    ngOnDestroy(): void {
        window.clearInterval(this.autoRefreshId);
    }

    getMessages() {
        this.messageService.getMessages(this.page, this.pageSize)
            .subscribe(
                messages => this.messages = messages,
                error => this.errorMessage = <any>error
            );
    }

    clearMessages() {
        this.messageService.clearMessages().subscribe(
            success => this.getMessages(),
            error => this.errorMessage = <any>error
        );
    }

    prev() {
        if (this.page > 0) {
            this.page--;
            this.getMessages();
        }
    }

    next() {
        if (this.messages && this.messages.length) {
            this.page++;
            this.getMessages();
        }
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

import {Component, OnDestroy, OnInit} from "@angular/core";
import {MessageService} from "../../services/message-service";
import {Message} from "../../model/scenario";
import {MessageFilter} from "../../model/filter";

@Component({
    moduleId: module.id,
    templateUrl: 'messages.html',
    styleUrls: ['messages.css']

})
export class MessagesComponent implements OnInit, OnDestroy {
    messages: Message[];
    errorMessage: string;

    messageFilter: MessageFilter;
    inboundState: string = 'active';
    outboundState: string = 'active';

    pageSize = 25;
    page = 0;

    autoRefreshId: number;

    constructor(private messageService: MessageService) {
    }

    ngOnInit() {
        this.messageFilter = this.initMessageFilter();
        this.getMessages();
        this.autoRefreshId = window.setInterval(() => {
            if (this.messageFilter.pageNumber == 0 && this.messageFilter.pageSize < 250) {
                this.getMessages();
            }
        }, 5000);
    }

    ngOnDestroy(): void {
        window.clearInterval(this.autoRefreshId);
    }

    getMessages() {
        this.messageService.getMessages(this.messageFilter)
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
        if (this.messageFilter.pageNumber > 0) {
            this.messageFilter.pageNumber--;
            this.getMessages();
        }
    }

    next() {
        if (!this.messages) {
            return;
        }
        let hasPotentialNextPage = this.messages.length && this.messages.length == this.messageFilter.pageSize;
        if (hasPotentialNextPage) {
            this.messageFilter.pageNumber++;
            this.getMessages();
        }
    }

    toggleInbound() {
        this.messageFilter.directionInbound = !this.messageFilter.directionInbound;
        if (this.messageFilter.directionInbound) {
            this.inboundState = 'active';
        } else {
            this.inboundState = '';
        }
        this.getMessages();
    }

    toggleOutbound() {
        this.messageFilter.directionOutbound = !this.messageFilter.directionOutbound;
        if (this.messageFilter.directionOutbound) {
            this.outboundState = 'active';
        } else {
            this.outboundState = '';
        }
        this.getMessages();
    }

    initMessageFilter(): MessageFilter {
        return new MessageFilter(null, null, 0, 25, true, true, '');
    }

    changePageSize(pageSize: number) {
        this.messageFilter.pageSize = pageSize;
        this.messageFilter.pageNumber = 0;
        this.getMessages();
    }
}

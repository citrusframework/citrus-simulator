import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {Message} from "../model/test";
import {ConfigService} from "./config-service";

@Injectable()
export class MessageService {

    constructor(private http:Http, private configService:ConfigService) {
    }

    getMessages(): Observable<Message[]> {
        return this.retrieveMessages();
    }

    getMessageById(id: number): Observable<Message> {
        return this.retrieveMessageById(id);
    }

    clearMessages(): Observable<any> {
        return this.deleteAllMessages();
    }

    private retrieveMessages(): Observable<Message[]> {
        let messageUrl = this.configService.getBaseUrl() + "message";
        return this.http.get(messageUrl)
            .map(
                this.extractMessageData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveMessageById(id: number): Observable<Message> {
        let messageIdUrl = this.configService.getBaseUrl() + "message/" + id;
        return this.http.get(messageIdUrl)
            .map(
                this.extractMessage
            )
            .catch(
                this.handleError
            );
    }

    private deleteAllMessages(): Observable<any> {
        let messageUrl = this.configService.getBaseUrl() + "message";
        return this.http.delete(messageUrl)
            .catch(
                this.handleError
            );
    }

    private extractMessageData(res: Response) {
        var messages = <Message[]> res.json();
        if (messages) {
            return messages;
        }
        return [];
    }

    private extractMessage(res: Response) {
        return <Message> res.json();
    }

    private handleError(error: any) {
        // TODO return Error Object
        let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg);
        return Observable.throw(errMsg);
    }
}

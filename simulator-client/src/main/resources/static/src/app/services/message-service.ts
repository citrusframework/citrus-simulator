import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {Message} from "../model/scenario";

@Injectable()
export class MessageService {

    constructor(private http:Http) {
    }

    private serviceUrl = 'api/message';

    getMessages(page: number, pageSize: number): Observable<Message[]> {
        return this.http.get(this.serviceUrl + "?page=" + page + "&size=" + pageSize)
            .map(this.extractMessageData)
            .catch(this.handleError);
    }

    getMessageById(id: number): Observable<Message> {
        return this.http.get(this.serviceUrl + "/" + id)
            .map(this.extractMessage)
            .catch(this.handleError);
    }

    clearMessages(): Observable<any> {
        return this.http.delete(this.serviceUrl)
            .catch(this.handleError);
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

import {Injectable} from "@angular/core";
import {Headers, Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {Message} from "../model/scenario";
import {MessageFilter} from "../model/filter";

@Injectable()
export class MessageService {

    constructor(private http:Http) {
    }

    private serviceUrl = 'api/message';

    getMessages(filter: MessageFilter): Observable<Message[]> {
        let headers = new Headers({'Content-Type': 'application/json'});
        return this.http.post(this.serviceUrl, JSON.stringify(filter), {headers: headers})
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
        let messages = <Message[]> res.json();
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

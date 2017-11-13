import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from 'rxjs/Observable';
import {Message} from "../model/scenario";
import {MessageFilter} from "../model/filter";

@Injectable()
export class MessageService {

    constructor(private http: HttpClient) {
    }

    private serviceUrl = 'api/message';

    getMessages(filter: MessageFilter): Observable<Message[]> {
        return this.http.post<Message[]>(
            this.serviceUrl,
            JSON.stringify(filter),
            {headers: new HttpHeaders().set('Content-Type', 'application/json')}
        );
    }

    getMessageById(id: number): Observable<Message> {
        return this.http.get<Message>(this.serviceUrl + "/" + id);
    }

    clearMessages(): Observable<any> {
        return this.http.delete(this.serviceUrl);
    }
}

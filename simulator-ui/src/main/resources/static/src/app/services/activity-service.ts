import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from 'rxjs';
import {ScenarioExecution} from "../model/scenario";
import {ScenarioExecutionFilter} from "../model/filter";

@Injectable()
export class ActivityService {

    constructor(private http: HttpClient) {
    }

    private serviceUrl = 'api/activity';

    getScenarioExecutions(filter: ScenarioExecutionFilter): Observable<ScenarioExecution[]> {
        return this.http.post<ScenarioExecution[]>(
            this.serviceUrl,
            JSON.stringify(filter),
            {headers: new HttpHeaders().set('Content-Type', 'application/json')}
        );
    }

    getScenarioExecutionById(id: number): Observable<ScenarioExecution> {
        return this.http.get<ScenarioExecution>(this.serviceUrl + "/" + id);
    }

    clearScenarioExecutions(): Observable<any> {
        return this.http.delete(this.serviceUrl);
    }
}

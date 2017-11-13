import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs/Observable';
import {ScenarioExecution} from "../model/scenario";

@Injectable()
export class ActivityService {

    constructor(private http: HttpClient) {
    }

    private serviceUrl = 'api/activity';

    getScenarioExecutions(page: number, pageSize: number): Observable<ScenarioExecution[]> {
        return this.http.get<ScenarioExecution[]>(this.serviceUrl + "?page=" + page + "&size=" + pageSize);
    }

    getScenarioExecutionById(id: number): Observable<ScenarioExecution> {
        return this.http.get<ScenarioExecution>(this.serviceUrl + "/" + id);
    }

    getScenarioExecutionsByScenarioName(name: string): Observable<ScenarioExecution[]> {
        return this.http.get<ScenarioExecution[]>(this.serviceUrl + "/scenario/" + name);
    }

    getScenarioExecutionsByExecutionStatus(status: string): Observable<ScenarioExecution[]> {
        return this.http.get<ScenarioExecution[]>(this.serviceUrl + "/status/" + status);
    }

    clearScenarioExecutions(): Observable<any> {
        return this.http.delete(this.serviceUrl);
    }
}

import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {ScenarioExecution} from "../model/scenario";

@Injectable()
export class ActivityService {

    constructor(private http:Http) {
    }

    private serviceUrl = 'api/activity';

    getScenarioExecutions(page: number, pageSize: number): Observable<ScenarioExecution[]> {
        return this.http.get(this.serviceUrl + "?page=" + page + "&size=" + pageSize)
            .map(this.extractScenarioExecutionData)
            .catch(this.handleError);
    }

    getScenarioExecutionById(id: number): Observable<ScenarioExecution> {
        return this.http.get(this.serviceUrl + "/" + id)
            .map(this.extractScenarioExecution)
            .catch(this.handleError);
    }

    getScenarioExecutionsByScenarioName(name: string): Observable<ScenarioExecution[]> {
        return this.http.get(this.serviceUrl + "/scenario/" + name)
            .map(this.extractScenarioExecutionData)
            .catch(this.handleError);
    }

    getScenarioExecutionsByExecutionStatus(status: string): Observable<ScenarioExecution[]> {
        return this.http.get(this.serviceUrl + "/status/" + status)
            .map(this.extractScenarioExecutionData)
            .catch(this.handleError);
    }

    clearScenarioExecutions(): Observable<any> {
        return this.http.delete(this.serviceUrl)
            .catch(this.handleError);
    }

    private extractScenarioExecutionData(res: Response) {
        var scenarioExecutionData = <ScenarioExecution[]> res.json();
        if (scenarioExecutionData) {
            return scenarioExecutionData;
        }
        return [];
    }

    private extractScenarioExecution(res: Response) {
        return <ScenarioExecution> res.json();
    }

    private handleError(error: any) {
        // TODO return Error Object
        let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg);
        return Observable.throw(errMsg);
    }
}

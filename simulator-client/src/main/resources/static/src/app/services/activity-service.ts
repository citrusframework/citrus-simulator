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

    getScenarioExecutions(): Observable<ScenarioExecution[]> {
        return this.retrieveScenarioExecutions();
    }

    getScenarioExecutionById(id: number): Observable<ScenarioExecution> {
        return this.retrieveScenarioExecutionById(id);
    }

    getScenarioExecutionsByScenarioName(name: string): Observable<ScenarioExecution[]> {
        return this.retrieveScenarioExecutionByName(name);
    }

    getScenarioExecutionsByExecutionStatus(status: string): Observable<ScenarioExecution[]> {
        return this.retrieveScenarioExecutionByStatus(status);
    }

    clearScenarioExecutions(): Observable<any> {
        return this.deleteAllScenarioExecutions();
    }

    private retrieveScenarioExecutions(): Observable<ScenarioExecution[]> {
        return this.http.get(this.serviceUrl)
            .map(
                this.extractScenarioExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveScenarioExecutionById(id: number): Observable<ScenarioExecution> {
        return this.http.get(this.serviceUrl + "/" + id)
            .map(
                this.extractScenarioExecution
            )
            .catch(
                this.handleError
            );
    }

    private retrieveScenarioExecutionByName(name: string): Observable<ScenarioExecution[]> {
        return this.http.get(this.serviceUrl + "/scenario/" + name)
            .map(
                this.extractScenarioExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveScenarioExecutionByStatus(status: string): Observable<ScenarioExecution[]> {
        return this.http.get(this.serviceUrl + "/status/" + status)
            .map(
                this.extractScenarioExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private deleteAllScenarioExecutions(): Observable<any> {
        return this.http.delete(this.serviceUrl)
            .catch(
                this.handleError
            );
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

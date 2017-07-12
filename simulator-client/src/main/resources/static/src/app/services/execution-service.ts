import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {ScenarioExecution} from "../model/scenario";
import {ConfigService} from "./config-service";

@Injectable()
export class ExecutionService {

    constructor(private http:Http, private configService:ConfigService) {
    }

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
        let activityUrl = this.configService.getBaseUrl() + "execution";
        return this.http.get(activityUrl)
            .map(
                this.extractScenarioExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveScenarioExecutionById(id: number): Observable<ScenarioExecution> {
        let executionByIdUrl = this.configService.getBaseUrl() + "execution/" + id;
        return this.http.get(executionByIdUrl)
            .map(
                this.extractScenarioExecution
            )
            .catch(
                this.handleError
            );
    }

    private retrieveScenarioExecutionByName(name: string): Observable<ScenarioExecution[]> {
        let executionsByNameUrl = this.configService.getBaseUrl() + "execution/scenario/" + name;
        return this.http.get(executionsByNameUrl)
            .map(
                this.extractScenarioExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveScenarioExecutionByStatus(status: string): Observable<ScenarioExecution[]> {
        let executionsByStatusUrl = this.configService.getBaseUrl() + "execution/status/" + status;
        return this.http.get(executionsByStatusUrl)
            .map(
                this.extractScenarioExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private deleteAllScenarioExecutions(): Observable<any> {
        let executionUrl = this.configService.getBaseUrl() + "execution";
        return this.http.delete(executionUrl)
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

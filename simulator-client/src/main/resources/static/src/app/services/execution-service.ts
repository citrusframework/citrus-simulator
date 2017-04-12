import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {TestExecution} from "../model/test";
import {ConfigService} from "./config-service";

@Injectable()
export class ExecutionService {

    constructor(private http:Http, private configService:ConfigService) {
    }

    getTestExecutions(): Observable<TestExecution[]> {
        return this.retrieveTestExecutions();
    }

    getTestExecutionById(id: number): Observable<TestExecution> {
        return this.retrieveTestExecutionById(id);
    }

    getTestExecutionsByTestName(name: string): Observable<TestExecution[]> {
        return this.retrieveTestExecutionByName(name);
    }

    getTestExecutionsByExecutionStatus(status: string): Observable<TestExecution[]> {
        return this.retrieveTestExecutionByStatus(status);
    }

    clearTestExecutions(): Observable<any> {
        return this.deleteAllTestExecutions();
    }

    private retrieveTestExecutions(): Observable<TestExecution[]> {
        let activityUrl = this.configService.getBaseUrl() + "execution";
        return this.http.get(activityUrl)
            .map(
                this.extractTestExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveTestExecutionById(id: number): Observable<TestExecution> {
        let executionByIdUrl = this.configService.getBaseUrl() + "execution/" + id;
        return this.http.get(executionByIdUrl)
            .map(
                this.extractTestExecution
            )
            .catch(
                this.handleError
            );
    }

    private retrieveTestExecutionByName(name: string): Observable<TestExecution[]> {
        let executionsByNameUrl = this.configService.getBaseUrl() + "execution/test/" + name;
        return this.http.get(executionsByNameUrl)
            .map(
                this.extractTestExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private retrieveTestExecutionByStatus(status: string): Observable<TestExecution[]> {
        let executionsByStatusUrl = this.configService.getBaseUrl() + "execution/status/" + status;
        return this.http.get(executionsByStatusUrl)
            .map(
                this.extractTestExecutionData
            )
            .catch(
                this.handleError
            );
    }

    private deleteAllTestExecutions(): Observable<any> {
        let executionUrl = this.configService.getBaseUrl() + "execution";
        return this.http.delete(executionUrl)
            .catch(
                this.handleError
            );
    }

    private extractTestExecutionData(res: Response) {
        var testExecutionData = <TestExecution[]> res.json();
        if (testExecutionData) {
            return testExecutionData;
        }
        return [];
    }

    private extractTestExecution(res: Response) {
        return <TestExecution> res.json();
    }

    private handleError(error: any) {
        // TODO return Error Object
        let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg);
        return Observable.throw(errMsg);
    }
}

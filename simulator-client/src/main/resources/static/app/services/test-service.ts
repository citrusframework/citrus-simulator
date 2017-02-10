import {Injectable} from "@angular/core";
import {Headers, Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {Test, TestParameter} from "../model/test";
import {ConfigService} from "./config-service";

@Injectable()
export class TestService {

    constructor(private http: Http, private configService: ConfigService) {
    }

    getTests(): Observable<Test[]> {
        return this.retrieveTests("");
    }

    getActiveTests(): Observable<Test[]> {
        return this.retrieveTests("?filter=active");
    }

    getTest(name: string): Observable<Test> {
        return this.retrieveTests("").map(tests => this.filterTestsByName(name, tests)).catch(this.handleError);
    }

    private filterTestsByName(name: string, tests: Test[]): Test {
        return tests.filter(test => test.name === name)[0]
    }

    getTestParameters(name: string): Observable<TestParameter[]> {
        let testParametersUrl = this.configService.getBaseUrl() + "test/" + name + "/parameters";
        return this.http.get(testParametersUrl)
            .map(
                this.extractTestParameterData
            )
            .catch(
                this.handleError
            );
    }

    launchTest(name: string, testParameters: TestParameter[]): Observable<any> {
        let launchUrl = this.configService.getBaseUrl() + "test/" + name + "/launch";
        let headers = new Headers({'Content-Type': 'application/json'});

        return this.http.post(launchUrl, JSON.stringify(testParameters), {headers: headers})
            .map(
                this.extractExecutionId
            )
            .catch(
                this.handleError
            );
    }

    private extractExecutionId(res: Response) {
        return <number> res.json();
    }

    private retrieveTests(filter: string): Observable<Test[]> {
        let testsUrl = this.configService.getBaseUrl() + "test" + filter;
        return this.http.get(testsUrl)
            .map(
                this.extractTestData
            )
            .catch(
                this.handleError
            );
    }

    private extractTestData(res: Response) {
        var tests = <Test[]> res.json();
        if (tests) {
            return tests;
        }
        return [];
    }

    private extractTestParameterData(res: Response) {
        var testParameters = <TestParameter[]> res.json();
        if (testParameters) {
            return testParameters;
        }
        return [];
    }

    private handleError(error: any) {
        // TODO return Error Object
        let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg);
        return Observable.throw(errMsg);
    }
}

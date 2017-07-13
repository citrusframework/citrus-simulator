import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {Summary} from "../model/scenario";

@Injectable()
export class SummaryService {

    constructor(private http:Http) {
    }

    private serviceUrl = 'api/summary';

    getSummary(): Observable<Summary> {
        return this.retrieveSummaryResults();
    }

    resetSummary(): Observable<Summary> {
        return this.resetSummaryResults();
    }

    getCountActiveScenarios(): Observable<number> {
        return this.retrieveSummaryActive();
    }

    private retrieveSummaryResults(): Observable<Summary> {
        return this.http.get(this.serviceUrl + "/results")
            .map(
                this.extractSummaryResults
            )
            .catch(
                this.handleError
            );
    }

    private resetSummaryResults(): Observable<Summary> {
        return this.http.delete(this.serviceUrl + "/results")
            .map(
                this.extractSummaryResults
            )
            .catch(
                this.handleError
            );
    }

    private retrieveSummaryActive(): Observable<number> {
        return this.http.get(this.serviceUrl + "/active")
            .map(
                this.extractSummaryActive
            )
            .catch(
                this.handleError
            );
    }

    private extractSummaryResults(res: Response) {
        return <Summary> res.json();
    }

    private extractSummaryActive(res: Response) {
        return <number> res.json();
    }

    private handleError(error: any) {
        // TODO return Error Object
        let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg);
        return Observable.throw(errMsg);
    }
}

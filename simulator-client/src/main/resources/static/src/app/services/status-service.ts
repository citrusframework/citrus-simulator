import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {Summary} from "../model/scenario";
import {ConfigService} from "./config-service";

@Injectable()
export class StatusService {

    constructor(private http:Http, private configService:ConfigService) {
    }

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
        let summaryResultsUrl = this.configService.getBaseUrl() + "summary/results";
        return this.http.get(summaryResultsUrl)
            .map(
                this.extractSummaryResults
            )
            .catch(
                this.handleError
            );
    }

    private resetSummaryResults(): Observable<Summary> {
        let summaryResultsUrl = this.configService.getBaseUrl() + "summary/results";
        return this.http.delete(summaryResultsUrl)
            .map(
                this.extractSummaryResults
            )
            .catch(
                this.handleError
            );
    }

    private retrieveSummaryActive(): Observable<number> {
        let summaryActiveUrl = this.configService.getBaseUrl() + "summary/active";
        return this.http.get(summaryActiveUrl)
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

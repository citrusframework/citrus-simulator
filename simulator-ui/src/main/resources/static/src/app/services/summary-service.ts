import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs/Observable';
import {Summary} from "../model/scenario";

@Injectable()
export class SummaryService {

    constructor(private http: HttpClient) {
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
        return this.http.get<Summary>(this.serviceUrl + "/results");
    }

    private resetSummaryResults(): Observable<Summary> {
        return this.http.delete<Summary>(this.serviceUrl + "/results");
    }

    private retrieveSummaryActive(): Observable<number> {
        return this.http.get<number>(this.serviceUrl + "/active");
    }
}

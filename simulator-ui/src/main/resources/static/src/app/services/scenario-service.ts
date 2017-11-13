import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from 'rxjs/Observable';
import {Scenario, ScenarioParameter} from "../model/scenario";
import {ScenarioFilter} from "../model/filter";

@Injectable()
export class ScenarioService {

    constructor(private http: HttpClient) {
    }

    private serviceUrl = 'api/scenario';

    getScenarios(): Observable<Scenario[]> {
        return this.retrieveScenarios(new ScenarioFilter(""));
    }

    getScenario(name: string): Observable<Scenario[]> {
        // TODO MM get first from Observable
        return this.retrieveScenarios(new ScenarioFilter(name));
    }

    getScenarioParameters(name: string): Observable<ScenarioParameter[]> {
        return this.http.get<ScenarioParameter[]>(this.serviceUrl + "/parameters/" + name);
    }

    launchScenario(name: string, scenarioParameters: ScenarioParameter[]): Observable<number> {
        return this.http.post<number>(
            this.serviceUrl + "/launch/" + name,
            JSON.stringify(scenarioParameters),
            {headers: new HttpHeaders().set('Content-Type', 'application/json')}
        );
    }

    private retrieveScenarios(filter: ScenarioFilter): Observable<Scenario[]> {
        return this.http.post<Scenario[]>(this.serviceUrl,
            JSON.stringify(filter),
            {headers: new HttpHeaders().set('Content-Type', 'application/json')}
        );
    }
}

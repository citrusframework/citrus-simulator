import {Injectable} from "@angular/core";
import {Simulator} from "../model/simulator";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';

@Injectable()
export class AppInfoService {
    constructor(private http:Http) {
    }

    private serviceUrl = "manage/info";

    cachedSimulator: Simulator;
    cachedObservable: Observable<Simulator>;

    getSimulatorInfo(): Observable<Simulator> {
        if (this.cachedSimulator) {
            return Observable.of(this.cachedSimulator)
        } else if (this.cachedObservable) {
            return this.cachedObservable;
        } else {
            this.cachedObservable = this.http.get(this.serviceUrl)
                .map(this.extractData)
                .do(p => this.cachedSimulator = p)
                .catch(this.handleError)
                .share();
            return this.cachedObservable;
        }
    }

    private extractData(res: Response): Simulator {
        let info = res.json();
        let name = info.simulator.name ? info.simulator.name : '';
        let domain = info.simulator.domain ? info.simulator.domain : '';
        let version = info.simulator.version ? info.simulator.version : '';
        return new Simulator(name, domain, version);
    }

    private handleError(error: Response | any) {
        let errMsg: string;
        if (error instanceof Response) {
            const body = error.json() || '';
            const err = body.error || JSON.stringify(body);
            errMsg = `Cannot get info. Error code: ${error.status} - ${error.statusText || ''}, URL: ${error.url}, Error: ${err}`;
        } else {
            errMsg = error.message ? error.message : error.toString();
        }
        return Observable.throw(errMsg);
    }
}

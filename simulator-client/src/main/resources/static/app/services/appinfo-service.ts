import {Injectable} from "@angular/core";
import {AppInfo} from "../model/appinfo";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {ConfigService} from "./config-service";

@Injectable()
export class AppInfoService {
    constructor(private http:Http, private configService:ConfigService) {
    }

    getAppInfo(): Observable<AppInfo> {
        let infoUrl = this.configService.getBaseUrl() + "manage/info";
        return this.http.get(infoUrl).map(this.extractData).catch(this.handleError);
    }

    private extractData(res: Response): AppInfo {
        let info = res.json();
        let simulatorName = info.simulator.name ? info.simulator.name : '';
        let simulatorDomain = info.simulator.domain ? info.simulator.domain : '';
        let simulatorVersion = info.simulator.version ? info.simulator.version : '';
        return new AppInfo(simulatorName, simulatorDomain, simulatorVersion);
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

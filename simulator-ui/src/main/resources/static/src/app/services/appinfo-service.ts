import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs/Observable';
import {AppInfo} from "../model/manage";

@Injectable()
export class AppInfoService {
    constructor(private http: HttpClient) {
    }

    private serviceUrl = "api/manage/info";

    getAppInfo(): Observable<AppInfo> {
        return this.http.get<AppInfo>(this.serviceUrl);
    }
}

import { Injectable }   from '@angular/core';

@Injectable()
export class ConfigService {
    getBaseUrl():string {
        return "http://localhost:8080/";
        //return "";
    }
}

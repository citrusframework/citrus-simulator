import { Injectable }   from '@angular/core';

@Injectable()
export class ConfigService {
    getBaseUrl():string {
        // hard-coded url useful when developing the gui standalone
        // with server running on another port
        // return "http://localhost:8080/";
        return "";
    }
}

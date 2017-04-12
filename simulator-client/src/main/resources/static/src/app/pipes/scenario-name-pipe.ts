import {Pipe, PipeTransform} from "@angular/core";
import {Test} from "../model/test";

@Pipe({
    name: 'scenarioFilter'
})
export class ScenarioNamePipe implements PipeTransform {
    transform(tests: Test[], name: string, starter: boolean, nonStarter: boolean): Test[] {
        if (tests) {
            let what = name.toLowerCase();
            return tests.filter(test => {
                let type = test.type.toLowerCase();

                if(!starter && type.indexOf('starter')) {
                    return false;
                }

                if(!nonStarter && type.indexOf('message_triggered')) {
                    return false;
                }

                if(name && name.length > 0) {
                    return ~test.name.toLowerCase().indexOf(name.toLowerCase());
                }
                return true;
            });
        } else {
            return tests;
        }
    }
}

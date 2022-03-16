import {Pipe, PipeTransform} from "@angular/core";
import {Scenario} from "../model/scenario";

@Pipe({
    name: 'scenarioFilter'
})
export class ScenarioNamePipe implements PipeTransform {
    transform(scenarios: Scenario[], name: string, starter: boolean, nonStarter: boolean): Scenario[] {
        if (scenarios) {
            return scenarios.filter(scenario => {
                let type = scenario.type.toLowerCase();

                if (!starter && type == 'starter') {
                    return false;
                }

                if (!nonStarter && type == 'message_triggered') {
                    return false;
                }

                if (name && name.length > 0) {
                    return ~scenario.name.toLowerCase().indexOf(name.toLowerCase());
                }
                return true;
            });
        } else {
            return scenarios;
        }
    }
}

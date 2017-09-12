import {Pipe, PipeTransform} from "@angular/core";
import {ScenarioExecution} from "../model/scenario";

@Pipe({
    name: 'activityFilter'
})
export class ActivityFilterPipe implements PipeTransform {
    transform(scenarioExecutions: ScenarioExecution[], name: string, success: boolean, failed: boolean, active: boolean): ScenarioExecution[] {
        if (scenarioExecutions) {
            let what = name.toLowerCase();
            return scenarioExecutions.filter(scenarioExecution => {
                let scanarioName = scenarioExecution.scenarioName.toLowerCase();
                let scanarioStatus = scenarioExecution.status.toLowerCase();

                if (!success && scanarioStatus.indexOf('success') > -1) {
                    return false;
                }

                if (!failed && scanarioStatus.indexOf('failed') > -1) {
                    return false;
                }

                if (!active && scanarioStatus.indexOf('active') > -1) {
                    return false;
                }

                if (name && name.length > 0) {
                    return ~scanarioName.indexOf(what);
                }
                return true;
            });
        } else {
            return scenarioExecutions;
        }
    }
}

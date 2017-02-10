import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
    name: 'executionStatus'
})
export class ExecutionStatusPipe implements PipeTransform {
    transform(value: string, args: any[]) {
        if (value) {
            if (value == "SUCCESS")
                return "success";
            if (value == "ACTIVE")
                return "info";
            if (value == "FAILED")
                return "danger";
        }
        return;
    }
}

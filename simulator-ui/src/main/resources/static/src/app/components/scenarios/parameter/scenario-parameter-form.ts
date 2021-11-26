import {Component, Input, OnInit, OnChanges, SimpleChange} from "@angular/core";
import {Router} from "@angular/router";
import {Location} from "@angular/common";
import {FormGroup} from "@angular/forms";
import {ScenarioParameter, Scenario} from "../../../model/scenario";
import {ScenarioService} from "../../../services/scenario-service";
import {ScenarioParameterService} from "../../../services/scenario-parameter-service";

@Component({
    moduleId: module.id,
    selector: 'scenario-parameter-form',
    templateUrl: 'scenario-parameter-form.html',
    providers: [ScenarioParameterService, ScenarioService]
})
export class ScenarioParameterFormComponent implements OnChanges, OnInit {
    @Input() scenario: Scenario;
    @Input() scenarioParameters: ScenarioParameter[] = [];
    form: FormGroup;
    errorMessage: string;

    constructor(
        private scenarioParameterControlService: ScenarioParameterService,
        private scenarioService: ScenarioService,
        private router: Router,
        private location: Location) {
    }

    ngOnInit() {
        this.renderForm();
    }

    ngOnChanges(changes: {[propName: string]: SimpleChange}) {
        this.renderForm();
    }

    onSubmit() {
        this.scenarioParameters.forEach(scenarioParameter => this.updateValue(scenarioParameter));
        this.scenarioService.launchScenario(this.scenario.name, this.scenarioParameters)
            .subscribe({
                next: (executionId) => {
                    this.router.navigate(['activity', executionId]);
                },
                error: (error) => this.errorMessage = <any>error
            });
    }

    private renderForm() {
        this.form = this.scenarioParameterControlService.toFormGroup(this.scenarioParameters)
    }

    private updateValue(scenarioParameter: ScenarioParameter) {
        scenarioParameter.value = this.form.controls[scenarioParameter.name].value;
    }

    goBack() {
        this.location.back();
        return false;
    }
}

export class Scenario {
    constructor(
        public name: string,
        public type: string) {
    }
}

export class ScenarioExecution {
    constructor(
        public executionId: number,
        public startDate: number,
        public endDate: number,
        public scenarioName: string,
        public status: string,
        public errorMessage: string,
        public scenarioParameters: Array<ScenarioParameter>,
        public scenarioActions: Array<ScenarioAction>) {
    }
}

export class ScenarioAction {
    constructor(
        public actionId: number,
        public name: string,
        public startDate: number,
        public endDate: number) {
    }
}

export class ScenarioParameter {
    constructor(
        public parameterId: number,
        public name: string,
        public value: string,
        public required: boolean,
        public label: string,
        public controlType: string,                     // textbox, dropdown, textarea
        public options: Array<ScenarioParameterOption>      // required for dropdown
    ) {
    }
}

export class ScenarioParameterOption {
    constructor(
        public key: string,
        public value: string) {
    }
}

export class Summary {
    constructor(
        public size: number,
        public skippedPercentage: string,
        public failedPercentage: string,
        public successPercentage: string,
        public failed: number,
        public success: number,
        public skipped: number) {
    }
}

export class Message {
    constructor(
        public messageId: number,
        public direction: string,
        public date: number,
        public payload: string) {
    }
}

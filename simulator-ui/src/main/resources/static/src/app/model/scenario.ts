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
        public scenarioActions: Array<ScenarioAction>,
        public scenarioMessages: Array<Message>) {
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
        public size: number = 0,
        public skippedPercentage: string,
        public failedPercentage: string,
        public successPercentage: string,
        public failed: number = 0,
        public success: number = 0,
        public skipped: number = 0) {
    }
}

export class Message {
    constructor(
        public messageId: number,
        public direction: string,
        public date: number,
        public scenarioExecutionId: number,
        public scenarioName: string,
        public payload: string,
        public headers: Array<MessageHeader>) {
    }
}

export class MessageHeader {
    constructor(
        public name: string,
        public value: string) {
    }
}

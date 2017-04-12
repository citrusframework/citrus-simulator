export class Test {
    constructor(
        public name: string,
        public type: string) {
    }
}

export class TestExecution {
    constructor(
        public executionId: number,
        public startDate: number,
        public endDate: number,
        public testName: string,
        public status: string,
        public errorMessage: string,
        public testParameters: Array<TestParameter>,
        public testActions: Array<TestAction>) {
    }
}

export class TestAction {
    constructor(
        public actionId: number,
        public name: string,
        public startDate: number,
        public endDate: number) {
    }
}

export class TestParameter {
    constructor(
        public parameterId: number,
        public name: string,
        public value: string,
        public required: boolean,
        public label: string,
        public controlType: string,                     // textbox, dropdown, textarea
        public options: Array<TestParameterOption>      // required for dropdown
    ) {
    }
}

export class TestParameterOption {
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

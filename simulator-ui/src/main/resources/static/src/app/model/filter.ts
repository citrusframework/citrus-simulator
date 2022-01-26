export class MessageFilter {
    constructor(public fromDate: string,
                public toDate: string,
                public pageNumber: number,
                public pageSize: number,
                public headerFilter: string,
                public directionInbound: boolean,
                public directionOutbound: boolean,
                public containingText: string) {
    }
}

export class ScenarioExecutionFilter {
    constructor(
        public fromDate: string,
        public toDate: string,
        public pageNumber: number,
        public pageSize: number,
        public headerFilter: string,
        public scenarioName: string,
        public executionStatus: string[],
    ) {
    }
}

export class ScenarioFilter {
    constructor(public name: string) {
    }
}

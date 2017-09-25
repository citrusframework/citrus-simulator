export class MessageFilter {
    constructor(public fromDate: number,
                public toDate: number,
                public pageNumber: number,
                public pageSize: number,
                public directionInbound: boolean,
                public directionOutbound: boolean,
                public containingText: string) {
    }
}

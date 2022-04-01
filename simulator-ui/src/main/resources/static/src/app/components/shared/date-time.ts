import * as moment from "moment";

export function convertTime(date: any, time: any): string {
    /* converts 12h to 24h */
    let t = moment(time, ["h:mm A"]).format("HH:mm");
    let d = date.split("/");
    let timeNum = t.split(':').map(Number);
    /* -1 because the month starts at index 0 */
    return new Date(d[2], d[0]-1, d[1], Number(timeNum[0]), Number(timeNum[1])).toISOString();
}
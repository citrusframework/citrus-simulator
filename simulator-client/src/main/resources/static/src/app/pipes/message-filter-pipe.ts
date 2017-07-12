import {Pipe, PipeTransform} from "@angular/core";
import {Message} from "../model/scenario";

@Pipe({
    name: 'messageFilter'
})
export class MessageFilterPipe implements PipeTransform {
    transform(messages: Message[], name: string, inbound: boolean, outbound: boolean): Message[] {
        if (messages) {
            let what = name.toLowerCase();
            return messages.filter(message => {
                let payload = message.payload.toLowerCase();
                let direction = message.direction.toLowerCase();

                if(!inbound && direction.indexOf('inbound')) {
                    return false;
                }

                if(!outbound && direction.indexOf('outbound')) {
                    return false;
                }

                if(name && name.length > 0) {
                    return ~payload.indexOf(what);
                }
                return true;
            });
        } else {
            return messages;
        }
    }
}

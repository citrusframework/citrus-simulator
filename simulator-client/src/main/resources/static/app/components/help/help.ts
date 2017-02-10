import {Component, OnInit} from '@angular/core';

@Component({
    moduleId: module.id,
    selector: 'simulator-help-page',
    templateUrl: 'help.html'
})
export class HelpComponent implements OnInit {
    title = 'Help';

    ngOnInit() {
    }
}

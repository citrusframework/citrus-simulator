import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {Test, TestParameter} from "../../model/test";
import {TestService} from "../../services/test-service";

@Component({
    moduleId: module.id,
    selector: 'test-launch',
    templateUrl: 'test-launch.html',
    providers: [TestService]
})
export class TestLaunchComponent implements OnInit {
    title = 'Test Launch';
    test: Test;
    testParameters: TestParameter[];
    errorMessage: string;

    constructor(
        private testService: TestService,
        private route: ActivatedRoute,
        private location: Location) {
    }

    ngOnInit() {
        let name = this.route.snapshot.params['name'];
        this.getTest(name);
        this.getTestParameters(name);
    }

    getTest(name: string) {
        this.testService.getTest(name)
            .subscribe(
                test => this.test = test,
                error => this.errorMessage = <any>error
            );
    }

    getTestParameters(name: string) {
        this.testService.getTestParameters(name)
            .subscribe(
                testParameters => this.testParameters = testParameters,
                error => this.errorMessage = <any>error
            );
    }

    goBack() {
        this.location.back();
        return false;
    }
}

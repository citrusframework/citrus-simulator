import { Component, OnInit } from '@angular/core';

import { map } from 'rxjs/operators';

import { TestResultsByStatus, TestResultService } from 'app/entities/test-result/service/test-result.service';
import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  selector: 'app-test-result-summary',
  templateUrl: './test-result-summary.component.html',
  imports: [SharedModule],
})
export default class TestResultSummaryComponent implements OnInit {
  testResults: TestResultsByStatus | null = null;

  successfulPercentage = 0;
  failedPercentage = 0;

  constructor(private testResultService: TestResultService) {}

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.testResultService
      .countByStatus()
      .pipe(map(response => response.body ?? { total: 0, successful: 0, failed: 0 }))
      .subscribe((testResults: TestResultsByStatus) => {
        this.testResults = testResults;

        if (testResults.total > 0) {
          this.successfulPercentage = (testResults.successful / testResults.total) * 100;
          this.failedPercentage = (testResults.failed / testResults.total) * 100;
        }
      });
  }
}

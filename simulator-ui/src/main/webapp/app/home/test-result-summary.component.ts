import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { filter, map } from 'rxjs/operators';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

import { STATUS_FAILURE, STATUS_SUCCESS } from 'app/entities/test-result/test-result.model';
import { TestResultsByStatus, TestResultService } from 'app/entities/test-result/service/test-result.service';

import { ProfileService } from 'app/layouts/profiles/profile.service';
import { SimulatorConfiguration } from 'app/layouts/profiles/profile-info.model';

import SharedModule from 'app/shared/shared.module';

import TestResultDeleteDialogComponent from './delete/test-result-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'app-test-result-summary',
  styleUrls: ['./test-result-summary.component.scss'],
  templateUrl: './test-result-summary.component.html',
  imports: [RouterModule, SharedModule],
})
export default class TestResultSummaryComponent implements OnInit {
  isLoading = false;
  testResults: TestResultsByStatus | null = null;

  successfulPercentage = '0';
  failedPercentage = '0';

  statusSuccess = STATUS_SUCCESS;
  statusFailed = STATUS_FAILURE;

  resetEnabled = this.profileService
    .getSimulatorConfiguration()
    .pipe(map((simulatorConfiguration: SimulatorConfiguration) => simulatorConfiguration.resetResultsEnabled));

  constructor(
    private modalService: NgbModal,
    private profileService: ProfileService,
    private testResultService: TestResultService,
  ) {}

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.isLoading = true;
    this.testResultService
      .countByStatus()
      .pipe(map(response => response.body ?? { total: 0, successful: 0, failed: 0 }))
      .subscribe({
        next: (testResults: TestResultsByStatus) => {
          this.testResults = testResults;

          if (testResults.total > 0) {
            this.successfulPercentage = this.toFixedDecimalIfNotMatching((testResults.successful / testResults.total) * 100);
            this.failedPercentage = this.toFixedDecimalIfNotMatching((testResults.failed / testResults.total) * 100);
          }
        },
        complete: () => (this.isLoading = false),
      });
  }

  protected reset(): void {
    const modalRef = this.modalService.open(TestResultDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.pipe(filter(reason => reason === ITEM_DELETED_EVENT)).subscribe({
      next: () => {
        this.load();
      },
    });
  }

  private toFixedDecimalIfNotMatching(percentage: number): string {
    const fixed = percentage.toFixed(2);
    return fixed.endsWith('.00') ? fixed.slice(0, fixed.length - 3) : fixed;
  }
}

import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITestResult } from '../test-result.model';

@Component({
  selector: 'app-test-result-detail',
  templateUrl: './test-result-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TestResultDetailComponent {
  testResult = input<ITestResult | null>(null);

  previousState(): void {
    window.history.back();
  }
}

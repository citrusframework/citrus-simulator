import { Component, Input, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITestResult } from '../test-result.model';

@Component({
  standalone: true,
  selector: 'app-test-result-detail',
  templateUrl: './test-result-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TestResultDetailComponent {
  @Input() testResult: ITestResult | null = null;
  protected activatedRoute = inject(ActivatedRoute);

  previousState(): void {
    window.history.back();
  }
}

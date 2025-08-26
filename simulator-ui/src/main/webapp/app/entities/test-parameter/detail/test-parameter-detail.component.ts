import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITestParameter } from '../test-parameter.model';

@Component({
  selector: 'app-test-parameter-detail',
  templateUrl: './test-parameter-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TestParameterDetailComponent {
  testParameter = input<ITestParameter | null>(null);

  previousState(): void {
    window.history.back();
  }
}

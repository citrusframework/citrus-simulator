import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITestParameter } from '../test-parameter.model';

@Component({
  standalone: true,
  selector: 'app-test-parameter-detail',
  templateUrl: './test-parameter-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TestParameterDetailComponent {
  @Input() testParameter: ITestParameter | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}

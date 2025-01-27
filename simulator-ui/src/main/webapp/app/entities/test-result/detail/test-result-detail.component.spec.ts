import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TestResultDetailComponent } from './test-result-detail.component';

describe('TestResult Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestResultDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TestResultDetailComponent,
              resolve: { testResult: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TestResultDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load testResult on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TestResultDetailComponent);

      // THEN
      expect(instance.testResult).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

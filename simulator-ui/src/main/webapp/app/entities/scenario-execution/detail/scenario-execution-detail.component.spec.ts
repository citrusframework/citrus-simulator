import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioExecutionDetailComponent } from './scenario-execution-detail.component';

describe('ScenarioExecution Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioExecutionDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ScenarioExecutionDetailComponent,
              resolve: { scenarioExecution: () => of({ executionId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ScenarioExecutionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load scenarioExecution on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioExecutionDetailComponent);

      // THEN
      expect(instance.scenarioExecution).toEqual(expect.objectContaining({ executionId: 123 }));
    });
  });
});

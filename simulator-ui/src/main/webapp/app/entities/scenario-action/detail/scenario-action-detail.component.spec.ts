import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioActionDetailComponent } from './scenario-action-detail.component';

describe('ScenarioAction Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioActionDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ScenarioActionDetailComponent,
              resolve: { scenarioAction: () => of({ actionId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ScenarioActionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load scenarioAction on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioActionDetailComponent);

      // THEN
      expect(instance.scenarioAction).toEqual(expect.objectContaining({ actionId: 123 }));
    });
  });
});

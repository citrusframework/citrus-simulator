import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioParameterDetailComponent } from './scenario-parameter-detail.component';

describe('ScenarioParameter Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioParameterDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ScenarioParameterDetailComponent,
              resolve: { scenarioParameter: () => of({ parameterId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ScenarioParameterDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load scenarioParameter on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioParameterDetailComponent);

      // THEN
      expect(instance.scenarioParameter).toEqual(expect.objectContaining({ parameterId: 123 }));
    });
  });
});

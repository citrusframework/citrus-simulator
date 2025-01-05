import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';

import { ScenarioDetailComponent } from './scenario-detail.component';

const scenarioParameters: IScenarioParameter[] = [{ parameterId: 123 }];

describe('Scenario Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [provideHttpClientTesting(), ScenarioDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ScenarioDetailComponent,
              resolve: { scenarioParameters: () => of(scenarioParameters) },
            },
          ],
          withComponentInputBinding(),
        ),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParamMap: of(jest.requireActual('@angular/router').convertToParamMap({ name: 'test-scenario', type: 'STARTER' })),
          },
        },
      ],
    })
      .overrideTemplate(ScenarioDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load scenario on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioDetailComponent);

      // THEN
      // expect(instance.name).toEqual('test-scenario');
      // expect(instance.type).toEqual('STARTER');
      expect(instance.scenarioParameters).toEqual(scenarioParameters);
    });
  });
});

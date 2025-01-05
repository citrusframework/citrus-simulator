import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioParameterDetailComponent } from './scenario-parameter-detail.component';

describe('ScenarioParameter Management Detail Component', () => {
  let comp: ScenarioParameterDetailComponent;
  let fixture: ComponentFixture<ScenarioParameterDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioParameterDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./scenario-parameter-detail.component').then(m => m.ScenarioParameterDetailComponent),
              resolve: { scenarioParameter: () => of({ parameterId: 31065 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ScenarioParameterDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScenarioParameterDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load scenarioParameter on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioParameterDetailComponent);

      // THEN
      expect(instance.scenarioParameter()).toEqual(expect.objectContaining({ parameterId: 31065 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioExecutionDetailComponent } from './scenario-execution-detail.component';

describe('ScenarioExecution Management Detail Component', () => {
  let comp: ScenarioExecutionDetailComponent;
  let fixture: ComponentFixture<ScenarioExecutionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioExecutionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./scenario-execution-detail.component').then(m => m.ScenarioExecutionDetailComponent),
              resolve: { scenarioExecution: () => of({ executionId: 7089 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ScenarioExecutionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScenarioExecutionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load scenarioExecution on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioExecutionDetailComponent);

      // THEN
      expect(instance.scenarioExecution()).toEqual(expect.objectContaining({ executionId: 7089 }));
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

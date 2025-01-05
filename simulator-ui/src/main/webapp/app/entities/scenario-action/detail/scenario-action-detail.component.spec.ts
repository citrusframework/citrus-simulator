import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioActionDetailComponent } from './scenario-action-detail.component';

describe('ScenarioAction Management Detail Component', () => {
  let comp: ScenarioActionDetailComponent;
  let fixture: ComponentFixture<ScenarioActionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScenarioActionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./scenario-action-detail.component').then(m => m.ScenarioActionDetailComponent),
              resolve: { scenarioAction: () => of({ actionId: 2674 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ScenarioActionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScenarioActionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load scenarioAction on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ScenarioActionDetailComponent);

      // THEN
      expect(instance.scenarioAction()).toEqual(expect.objectContaining({ actionId: 2674 }));
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

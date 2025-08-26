import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TestResultDetailComponent } from './test-result-detail.component';

describe('TestResult Management Detail Component', () => {
  let comp: TestResultDetailComponent;
  let fixture: ComponentFixture<TestResultDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestResultDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./test-result-detail.component').then(m => m.TestResultDetailComponent),
              resolve: { testResult: () => of({ id: 15012 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TestResultDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestResultDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load testResult on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TestResultDetailComponent);

      // THEN
      expect(instance.testResult()).toEqual(expect.objectContaining({ id: 15012 }));
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

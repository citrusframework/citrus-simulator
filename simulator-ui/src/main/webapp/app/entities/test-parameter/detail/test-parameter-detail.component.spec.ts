import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TestParameterDetailComponent } from './test-parameter-detail.component';

describe('TestParameter Management Detail Component', () => {
  let comp: TestParameterDetailComponent;
  let fixture: ComponentFixture<TestParameterDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestParameterDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./test-parameter-detail.component').then(m => m.TestParameterDetailComponent),
              resolve: { testParameter: () => of({ id: 3696 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TestParameterDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestParameterDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load testParameter on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TestParameterDetailComponent);

      // THEN
      expect(instance.testParameter()).toEqual(expect.objectContaining({ id: 3696 }));
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

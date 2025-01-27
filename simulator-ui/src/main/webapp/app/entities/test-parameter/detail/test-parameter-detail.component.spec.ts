import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TestParameterDetailComponent } from './test-parameter-detail.component';

describe('TestParameter Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestParameterDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TestParameterDetailComponent,
              resolve: { testParameter: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TestParameterDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load testParameter on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TestParameterDetailComponent);

      // THEN
      expect(instance.testParameter).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

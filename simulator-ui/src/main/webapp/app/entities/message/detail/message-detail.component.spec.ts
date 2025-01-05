import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MessageDetailComponent } from './message-detail.component';

describe('Message Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MessageDetailComponent,
              resolve: { message: () => of({ messageId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MessageDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load message on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MessageDetailComponent);

      // THEN
      expect(instance.message).toEqual(expect.objectContaining({ messageId: 123 }));
    });
  });
});

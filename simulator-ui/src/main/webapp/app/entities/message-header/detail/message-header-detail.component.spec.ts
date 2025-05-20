import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MessageHeaderDetailComponent } from './message-header-detail.component';

describe('MessageHeader Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageHeaderDetailComponent, provideRouter([], withComponentInputBinding())],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MessageHeaderDetailComponent,
              resolve: { messageHeader: () => of({ headerId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MessageHeaderDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('should load messageHeader on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MessageHeaderDetailComponent);

      // THEN
      expect(instance.messageHeader).toEqual(expect.objectContaining({ headerId: 123 }));
    });
  });
});

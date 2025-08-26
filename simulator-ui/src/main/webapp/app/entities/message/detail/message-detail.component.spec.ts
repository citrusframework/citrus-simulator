import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MessageDetailComponent } from './message-detail.component';

describe('Message Management Detail Component', () => {
  let comp: MessageDetailComponent;
  let fixture: ComponentFixture<MessageDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./message-detail.component').then(m => m.MessageDetailComponent),
              resolve: { message: () => of({ messageId: 6456 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MessageDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load message on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MessageDetailComponent);

      // THEN
      expect(instance.message()).toEqual(expect.objectContaining({ messageId: 6456 }));
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

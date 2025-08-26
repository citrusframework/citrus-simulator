import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MessageHeaderDetailComponent } from './message-header-detail.component';

describe('MessageHeader Management Detail Component', () => {
  let comp: MessageHeaderDetailComponent;
  let fixture: ComponentFixture<MessageHeaderDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageHeaderDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./message-header-detail.component').then(m => m.MessageHeaderDetailComponent),
              resolve: { messageHeader: () => of({ headerId: 21098 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MessageHeaderDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageHeaderDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load messageHeader on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MessageHeaderDetailComponent);

      // THEN
      expect(instance.messageHeader()).toEqual(expect.objectContaining({ headerId: 21098 }));
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

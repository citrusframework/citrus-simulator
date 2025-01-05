import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import HeaderFilterHelpDialogComponent from './header-filter-help-dialog.component';

describe('HeaderFilterDialogComponent', () => {
  let component: HeaderFilterHelpDialogComponent;
  let fixture: ComponentFixture<HeaderFilterHelpDialogComponent>;
  let activeModal: NgbActiveModal;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderFilterHelpDialogComponent],
      providers: [
        {
          provide: NgbActiveModal,
          useValue: {
            close: jest.fn(),
            dismiss: jest.fn(),
          },
        },
      ],
    })
      .overrideTemplate(HeaderFilterHelpDialogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HeaderFilterHelpDialogComponent);
    component = fixture.componentInstance;
    activeModal = TestBed.inject(NgbActiveModal);
  });

  describe('cancel', () => {
    it('should dismiss the modal on cancel', () => {
      // @ts-expect-error: Access protected function for testing
      component.cancel();

      expect(activeModal.dismiss).toHaveBeenCalled();
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import HeaderFilterDialogComponent, { ComparatorType, ValueType } from './header-filter-dialog.component';

describe('HeaderFilterDialogComponent', () => {
  let component: HeaderFilterDialogComponent;
  let fixture: ComponentFixture<HeaderFilterDialogComponent>;
  let activeModal: NgbActiveModal;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, HeaderFilterDialogComponent],
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
      .overrideTemplate(HeaderFilterDialogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HeaderFilterDialogComponent);
    component = fixture.componentInstance;
    activeModal = TestBed.inject(NgbActiveModal);
  });

  describe('ngOnInit', () => {
    it('should add one initial header filter', () => {
      expect(component.headerFilters.length).toBe(0);

      component.ngOnInit();

      expect(component.headerFilters.length).toBe(1);
      const formGroup = component.headerFilters[0];
      expect(formGroup.get('key')?.value).toBe('');
      expect(formGroup.get('keyComparator')?.value).toBe(ComparatorType.EQUALS);
      expect(formGroup.get('keyComparator')?.disabled).toBeTruthy();
      expect(formGroup.get('value')?.value).toBe('');
      expect(formGroup.get('valueType')?.value).toBe(ValueType.LITERAL);
      expect(formGroup.get('valueComparator')?.value).toBe(ComparatorType.EQUALS);
    });
  });

  describe('addNewHeaderFilter', () => {
    it('should add a new header filter', () => {
      // expect(component.headerFilters.length).toBe( 1);

      // @ts-expect-error: Access protected function for testing
      component.addNewHeaderFilter();

      expect(component.headerFilters.length).toBe(1);
    });
  });

  describe('getHeaderValueInputType', () => {
    it('default literal is text', () => {
      // @ts-expect-error: Access protected function for testing
      expect(component.getHeaderValueInputType(0)).toBe('text'); // Default LITERAL
    });

    it('should get header value input type based on value type', () => {
      // @ts-expect-error: Access protected function for testing
      component.addNewHeaderFilter();

      component.headerFilters[0].get('valueType')?.setValue(ValueType.NUMERICAL);
      fixture.detectChanges();

      // @ts-expect-error: Access protected function for testing
      expect(component.getHeaderValueInputType(0)).toBe('number');
    });
  });

  describe('addNewHeaderFilter', () => {
    it('should remove a header filter', () => {
      // @ts-expect-error: Access protected function for testing
      component.addNewHeaderFilter();
      // @ts-expect-error: Access protected function for testing
      component.addNewHeaderFilter();

      expect(component.headerFilters.length).toBe(2);

      // @ts-expect-error: Access protected function for testing
      component.removeHeaderFilter(1);

      expect(component.headerFilters.length).toBe(1);
    });

    it('should not remove the first header filter', () => {
      // @ts-expect-error: Access protected function for testing
      component.addNewHeaderFilter();

      // @ts-expect-error: Access protected function for testing
      component.removeHeaderFilter(0);

      expect(component.headerFilters.length).toBe(1);
    });
  });

  describe('submit', () => {
    it('should close the modal on submit', () => {
      // @ts-expect-error: Access protected function for testing
      component.submit();

      expect(activeModal.close).toHaveBeenCalledWith(component.headerFilters);
    });
  });

  describe('cancel', () => {
    it('should dismiss the modal on cancel', () => {
      // @ts-expect-error: Access protected function for testing
      component.cancel();

      expect(activeModal.dismiss).toHaveBeenCalled();
    });
  });
});

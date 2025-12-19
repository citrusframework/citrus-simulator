import { TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { ParamsDialogComponent } from './params-dialog.component';

describe('ParamsDialogComponent', () => {
  let component: ParamsDialogComponent;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    mockActiveModal = {
      close: jest.fn(),
      dismiss: jest.fn(),
    } as unknown as NgbActiveModal;

    TestBed.configureTestingModule({
      imports: [ParamsDialogComponent, TranslateModule.forRoot()],
      providers: [{ provide: NgbActiveModal, useValue: mockActiveModal }],
    });

    component = TestBed.createComponent(ParamsDialogComponent).componentInstance;
  });

  it('should initialize with an empty params array', () => {
    expect(component.params).toEqual([]);
  });

  it('should call activeModal.close with params on launch', () => {
    const mockParams = [{ name: 'param1', value: 'value1' }];
    component.params = mockParams;

    component.launch();

    expect(mockActiveModal.close).toHaveBeenCalledWith(mockParams);
  });
});

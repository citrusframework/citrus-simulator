import { ParamsDialogComponent } from './params-dialog.component';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

describe('ParamsDialogComponent', () => {
  let component: ParamsDialogComponent;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    mockActiveModal = {
      close: jest.fn(),
      dismiss: jest.fn(),
    } as unknown as NgbActiveModal;

    component = new ParamsDialogComponent(mockActiveModal);
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

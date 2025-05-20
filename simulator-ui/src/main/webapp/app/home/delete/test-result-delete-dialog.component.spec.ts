jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { of } from 'rxjs';

import { TestResultService } from 'app/entities/test-result/service/test-result.service';

import TestResultDeleteDialogComponent from './test-result-delete-dialog.component';

describe('Test Result Delete Component', () => {
  let service: TestResultService;
  let mockActiveModal: NgbActiveModal;

  let fixture: ComponentFixture<TestResultDeleteDialogComponent>;
  let component: TestResultDeleteDialogComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting(), TestResultDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(TestResultDeleteDialogComponent, '')
      .compileComponents();

    service = TestBed.inject(TestResultService);
    mockActiveModal = TestBed.inject(NgbActiveModal);

    fixture = TestBed.createComponent(TestResultDeleteDialogComponent);
    component = fixture.componentInstance;
  });

  describe('confirmDelete', () => {
    it('should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        jest.spyOn(service, 'deleteAll').mockReturnValue(of(new HttpResponse<void>()));

        component.confirmDelete();
        tick();

        expect(service.deleteAll).toHaveBeenCalled();
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('should not call delete service on clear', () => {
      jest.spyOn(service, 'deleteAll');

      component.cancel();

      expect(service.deleteAll).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});

jest.mock('app/core/util/alert.service');

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AlertService } from 'app/core/util/alert.service';

import { AlertComponent } from './alert.component';

describe('Alert Component', () => {
  let alertService: AlertService;

  let component: AlertComponent;
  let fixture: ComponentFixture<AlertComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [AlertComponent],
      providers: [AlertService],
    })
      .overrideTemplate(AlertComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertComponent);
    component = fixture.componentInstance;
    alertService = TestBed.inject(AlertService);
  });

  it('should call alertService.get on init', () => {
    // WHEN
    component.ngOnInit();

    // THEN
    expect(alertService.get).toHaveBeenCalled();
  });

  it('should call alertService.clear on destroy', () => {
    // WHEN
    component.ngOnDestroy();

    // THEN
    expect(alertService.clear).toHaveBeenCalled();
  });
});

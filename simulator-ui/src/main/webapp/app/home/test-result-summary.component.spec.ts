import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { TranslateService } from '@ngx-translate/core';

import { of } from 'rxjs';

import { TestResultsByStatus, TestResultService } from 'app/entities/test-result/service/test-result.service';

import { ProfileService } from 'app/layouts/profiles/profile.service';

import TestResultSummaryComponent from './test-result-summary.component';

import Mocked = jest.Mocked;
import { SimulatorConfiguration } from '../layouts/profiles/profile-info.model';

describe('TestResultSummaryComponent', () => {
  let testProfileService: Mocked<ProfileService>;
  let testResultService: Mocked<TestResultService>;

  let fixture: ComponentFixture<TestResultSummaryComponent>;
  let component: TestResultSummaryComponent;

  beforeEach(async () => {
    testProfileService = {
      getSimulatorConfiguration: jest.fn().mockReturnValueOnce(of({ resetResultsEnabled: true } as SimulatorConfiguration)),
    } as unknown as Mocked<ProfileService>;

    testResultService = {
      countByStatus: jest.fn(),
    } as unknown as Mocked<TestResultService>;

    await TestBed.configureTestingModule({
      imports: [TestResultSummaryComponent],
      providers: [
        { provide: ProfileService, useValue: testProfileService },
        { provide: TestResultService, useValue: testResultService },
        TranslateService,
      ],
    })
      .overrideTemplate(TestResultSummaryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestResultSummaryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should subscribe to simulator configuration', () => {
      expect(testProfileService.getSimulatorConfiguration).toHaveBeenCalled();
      expect(component.resetEnabled).toBeTruthy();
    });

    it('should correctly calculate fixed percentages', fakeAsync(() => {
      const mockData = new HttpResponse({
        body: {
          total: 3,
          successful: 2,
          failed: 1,
        },
      });

      testResultService.countByStatus.mockReturnValue(of(mockData));

      component.ngOnInit();
      tick();

      expect(component.testResults).toEqual(mockData.body);
      expect(component.successfulPercentage).toEqual('66.67');
      expect(component.failedPercentage).toEqual('33.33');
    }));

    it('should return even numbers with even results', fakeAsync(() => {
      const mockData = new HttpResponse({
        body: {
          total: 2,
          successful: 1,
          failed: 1,
        },
      });

      testResultService.countByStatus.mockReturnValue(of(mockData));

      component.ngOnInit();
      tick();

      expect(component.testResults).toEqual(mockData.body);
      expect(component.successfulPercentage).toEqual('50');
      expect(component.failedPercentage).toEqual('50');
    }));

    it('default to a zero-result', fakeAsync(() => {
      const mockData = new HttpResponse<TestResultsByStatus>({ body: null });

      testResultService.countByStatus.mockReturnValue(of(mockData));

      component.ngOnInit();
      tick();

      expect(component.testResults).toEqual({
        total: 0,
        successful: 0,
        failed: 0,
      });

      expect(component.successfulPercentage).toEqual('0');
      expect(component.failedPercentage).toEqual('0');
    }));
  });
});

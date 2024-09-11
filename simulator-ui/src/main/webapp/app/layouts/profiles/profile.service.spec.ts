import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { ProfileService } from './profile.service';
import { InfoResponse, SimulatorConfiguration, SimulatorInfo } from './profile-info.model';

import DoneCallback = jest.DoneCallback;

describe('ProfileService', () => {
  let applicationConfigServiceSpy: jest.Mocked<ApplicationConfigService>;

  let httpMock: HttpTestingController;

  let service: ProfileService;

  beforeEach(() => {
    applicationConfigServiceSpy = {
      getEndpointFor: jest.fn().mockReturnValue('mock-url'),
    } as any;

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ProfileService,
        { provide: ApplicationConfigService, useValue: applicationConfigServiceSpy },
      ],
    });

    httpMock = TestBed.inject(HttpTestingController);

    service = TestBed.inject(ProfileService);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure that there are no outstanding HTTP requests.
  });

  describe('getSimulatorConfiguration', () => {
    it.each([
      { resetResultsEnabled: 'true', expectedResult: true },
      { resetResultsEnabled: 'TRUE', expectedResult: true },
      { resetResultsEnabled: 'false', expectedResult: false },
      { resetResultsEnabled: 'FALSE', expectedResult: false },
      { resetResultsEnabled: 'any string', expectedResult: false },
    ])(
      'should retrieve the simulator configuration',
      ({ resetResultsEnabled, expectedResult }: { resetResultsEnabled: string; expectedResult: boolean }, done: DoneCallback) => {
        const mockResponse: InfoResponse = {
          config: {
            resetResultsEnabled: false,
            'reset-results-enabled': resetResultsEnabled,
          },
        };

        service.getSimulatorConfiguration().subscribe((simulatorConfiguration: SimulatorConfiguration) => {
          expect(simulatorConfiguration.resetResultsEnabled).toEqual(expectedResult);
          done();
        });

        const req = httpMock.expectOne('mock-url');
        expect(req.request.method).toBe('GET');
        req.flush(mockResponse);
      },
    );
  });

  describe('getSimulatorInfo', () => {
    it('should retrieve the simulator info', (done: DoneCallback) => {
      const simulator: SimulatorInfo = { name: 'Test Simulator', version: '1.2.3' };
      const mockResponse: InfoResponse = { simulator };

      service.getSimulatorInfo().subscribe((simulatorInfo: SimulatorInfo) => {
        expect(simulatorInfo).toEqual(simulator);
        done();
      });

      const req = httpMock.expectOne('mock-url');
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should cache the simulator info and not make another HTTP call on subsequent requests', () => {
      const simulator: SimulatorInfo = { name: 'Test Simulator', version: '1.2.3' };
      const mockResponse: InfoResponse = { simulator };

      service.getSimulatorInfo().subscribe();
      httpMock.expectOne('mock-url').flush(mockResponse);

      service.getSimulatorInfo().subscribe();
      httpMock.expectNone('mock-url'); // No additional HTTP call should be made.
    });
  });
});

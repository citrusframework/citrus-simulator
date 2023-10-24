import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { ProfileService } from './profile.service';

describe('ProfileService', () => {
  let service: ProfileService;
  let httpMock: HttpTestingController;
  let applicationConfigServiceSpy: jest.Mocked<ApplicationConfigService>;

  beforeEach(() => {
    applicationConfigServiceSpy = {
      getEndpointFor: jest.fn().mockReturnValue('mock-url'),
    } as any;

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProfileService, { provide: ApplicationConfigService, useValue: applicationConfigServiceSpy }],
    });

    service = TestBed.inject(ProfileService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure that there are no outstanding HTTP requests.
  });

  it('should retrieve the profile info', () => {
    const mockResponse: any = {
      activeProfiles: ['prod', 'api-docs'],
      'display-ribbon-on-profiles': 'prod,api-docs',
    };

    service.getProfileInfo().subscribe(profile => {
      expect(profile).toEqual({
        activeProfiles: ['prod', 'api-docs'],
        inProduction: true,
        openAPIEnabled: true,
        ribbonEnv: 'prod',
      });
    });

    const req = httpMock.expectOne('mock-url');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should cache the profile info and not make another HTTP call on subsequent requests', () => {
    const mockResponse: any = {
      activeProfiles: ['prod', 'api-docs'],
      'display-ribbon-on-profiles': 'prod,api-docs',
    };

    service.getProfileInfo().subscribe();
    httpMock.expectOne('mock-url').flush(mockResponse);

    service.getProfileInfo().subscribe();
    httpMock.expectNone('mock-url'); // No additional HTTP call should be made.
  });
});

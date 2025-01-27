import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { InfoResponse } from 'app/layouts/profiles/profile-info.model';

import HomeComponent from './home.component';
import { provideHttpClient } from '@angular/common/http';

describe('Home Component', () => {
  let applicationConfigService: ApplicationConfigService;
  let httpMock: HttpTestingController;

  let fixture: ComponentFixture<HomeComponent>;
  let component: HomeComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HomeComponent, provideHttpClient(), provideHttpClientTesting()],
      providers: [ApplicationConfigService],
    })
      .overrideTemplate(HomeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    applicationConfigService = TestBed.inject(ApplicationConfigService);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure that there are no outstanding HTTP requests.
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch simulatorInfo on initialization', fakeAsync(() => {
    const mockInfoResponse: InfoResponse = {
      simulator: {
        name: 'Citrus Simulator',
        version: '1.2.3',
      },
    };

    component.ngOnInit();

    const req = httpMock.expectOne(applicationConfigService.getEndpointFor('api/manage/info'));
    expect(req.request.method).toBe('GET');
    req.flush(mockInfoResponse);

    tick();

    expect(component.simulatorInfo).toEqual(mockInfoResponse.simulator);
  }));
});

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { InfoResponse, SimulatorInfo, SimulatorConfiguration } from './profile-info.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private infoUrl = this.applicationConfigService.getEndpointFor('api/manage/info');

  private simulatorInfo$?: Observable<SimulatorInfo>;

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  getSimulatorConfiguration(): Observable<SimulatorConfiguration> {
    return this.http.get<InfoResponse>(this.infoUrl).pipe(
      map(
        (response: InfoResponse) =>
          ({
            resetResultsEnabled: response.config?.['reset-results-enabled'].toLowerCase() === 'true',
          }) as SimulatorConfiguration,
      ),
      shareReplay(),
    );
  }

  getSimulatorInfo(): Observable<SimulatorInfo> {
    if (this.simulatorInfo$) {
      return this.simulatorInfo$;
    }

    this.simulatorInfo$ = this.http.get<InfoResponse>(this.infoUrl).pipe(
      // eslint-disable-next-line @typescript-eslint/no-misused-spread
      map((response: InfoResponse) => ({ ...response.simulator }) as SimulatorInfo),
      shareReplay(),
    );

    return this.simulatorInfo$;
  }
}

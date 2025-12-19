import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { InfoResponse, SimulatorInfo, SimulatorConfiguration } from './profile-info.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private simulatorInfo$?: Observable<SimulatorInfo>;

  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private infoUrl = this.applicationConfigService.getEndpointFor('api/manage/info');

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
      map((response: InfoResponse) => ({ ...response.simulator })),
      shareReplay(),
    );

    return this.simulatorInfo$;
  }
}

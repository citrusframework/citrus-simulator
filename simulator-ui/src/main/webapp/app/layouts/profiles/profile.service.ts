import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { ProfileInfo, InfoResponse } from './profile-info.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private infoUrl = this.applicationConfigService.getEndpointFor('api/manage/info');
  private profileInfo$?: Observable<ProfileInfo>;

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  getProfileInfo(): Observable<ProfileInfo> {
    if (this.profileInfo$) {
      return this.profileInfo$;
    }

    this.profileInfo$ = this.http.get<InfoResponse>(this.infoUrl).pipe(
      map((response: InfoResponse) => {
        const profileInfo: ProfileInfo = {
          activeProfiles: response.activeProfiles,
          inProduction: response.activeProfiles?.includes('prod'),
        };
        if (response.activeProfiles && response['display-ribbon-on-profiles']) {
          const displayRibbonOnProfiles = response['display-ribbon-on-profiles'].split(',');
          const ribbonProfiles = displayRibbonOnProfiles.filter(profile => response.activeProfiles?.includes(profile));
          if (ribbonProfiles.length > 0) {
            profileInfo.ribbonEnv = ribbonProfiles[0];
          }
        }
        return profileInfo;
      }),
      shareReplay(),
    );
    return this.profileInfo$;
  }
}

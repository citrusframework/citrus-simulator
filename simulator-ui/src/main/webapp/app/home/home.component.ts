import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { InfoResponse, SimulatorInfo } from 'app/layouts/profiles/profile-info.model';
import SharedModule from 'app/shared/shared.module';

import TestResultSummaryComponent from './test-result-summary.component';

@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [RouterModule, SharedModule, TestResultSummaryComponent],
})
export default class HomeComponent implements OnInit {
  simulatorInfo: SimulatorInfo | null = null;

  private infoUrl = this.applicationConfigService.getEndpointFor('api/manage/info');

  constructor(
    private applicationConfigService: ApplicationConfigService,
    private http: HttpClient,
  ) {}

  ngOnInit(): void {
    this.http.get<InfoResponse>(this.infoUrl).subscribe((response: InfoResponse) => {
      this.simulatorInfo = response.simulator ?? null;
    });
  }
}

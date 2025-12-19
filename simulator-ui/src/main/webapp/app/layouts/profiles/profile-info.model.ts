export interface InfoResponse {
  git?: any;
  build?: any;
  activeProfiles?: string[];
  simulator?: SimulatorInfo;
  config?: SimulatorConfiguration & {
    'reset-results-enabled': string;
  };
}

export class SimulatorInfo {
  constructor(
    public name?: string,
    public version?: string,
  ) {}
}

export class SimulatorConfiguration {
  constructor(public resetResultsEnabled = true) {}
}

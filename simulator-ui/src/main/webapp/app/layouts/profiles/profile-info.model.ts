export interface InfoResponse {
  'display-ribbon-on-profiles'?: string;
  git?: any;
  build?: any;
  activeProfiles?: string[];
  simulator?: SimulatorInfo;
}

export class ProfileInfo {
  constructor(
    public activeProfiles?: string[],
    public ribbonEnv?: string,
    public inProduction?: boolean,
    public openAPIEnabled?: boolean,
  ) {}
}

export class SimulatorInfo {
  constructor(
    public name?: string,
    public version?: string,
  ) {}
}

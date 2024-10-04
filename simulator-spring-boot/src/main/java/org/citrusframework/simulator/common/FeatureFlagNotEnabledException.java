package org.citrusframework.simulator.common;

import lombok.Getter;

@Getter
public class FeatureFlagNotEnabledException extends Exception {

  private final String flag;

  public FeatureFlagNotEnabledException(String flag) {
    this.flag = flag;
  }
}

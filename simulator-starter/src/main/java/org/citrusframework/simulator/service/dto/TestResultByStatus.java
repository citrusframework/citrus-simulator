package org.citrusframework.simulator.service.dto;

import java.util.Objects;

public record TestResultByStatus(Long successful, Long failed, Long total) {

    public TestResultByStatus(Long successful, Long failed) {
        this(Objects.isNull(successful) ? 0 : successful, Objects.isNull(failed)?0: failed, Objects.isNull(successful) || Objects.isNull(failed) ? 0: successful + failed);
    }
}

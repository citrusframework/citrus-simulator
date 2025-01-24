package org.citrusframework.simulator.web.rest;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
class TransactionUtils {

    @Transactional
    void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }
}

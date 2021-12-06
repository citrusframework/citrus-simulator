package org.citrusframework.simulator.sample.jms.async.scenario;

import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Christoph Deppisch
 */
public class AbstractFaxScenario extends AbstractSimulatorScenario {

    /** Fax payload helper */
    private PayloadHelper payloadHelper = new PayloadHelper();

    @Autowired
    @Qualifier("simulatorJmsStatusEndpoint")
    private JmsEndpoint statusEndpoint;

    /**
     * Gets the payloadHelper.
     *
     * @return
     */
    public PayloadHelper getPayloadHelper() {
        return payloadHelper;
    }

    /**
     * Gets the statusEndpoint.
     *
     * @return
     */
    public JmsEndpoint getStatusEndpoint() {
        return statusEndpoint;
    }
}

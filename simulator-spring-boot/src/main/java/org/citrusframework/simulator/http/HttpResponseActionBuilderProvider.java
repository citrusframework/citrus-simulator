package org.citrusframework.simulator.http;

import io.apicurio.datamodels.openapi.models.OasOperation;
import org.citrusframework.http.actions.HttpServerResponseActionBuilder;
import org.citrusframework.message.Message;

/**
 * Interface for providing an {@link HttpServerResponseActionBuilder} based on an OpenAPI operation and a received message.
 */
public interface HttpResponseActionBuilderProvider {

    HttpServerResponseActionBuilder provideHttpServerResponseActionBuilder(OasOperation oasOperation,
        Message receivedMessage);

}

package org.citrusframework.simulator.ws;

/**
 * @author Christoph Deppisch
 */
public interface SoapBuilderSupport<T> {

    void configure(T builder);
}

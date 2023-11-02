package org.citrusframework.simulator.http;

/**
 * @author Christoph Deppisch
 */
public interface HttpBuilderSupport<T> {

    void configure(T builder);
}

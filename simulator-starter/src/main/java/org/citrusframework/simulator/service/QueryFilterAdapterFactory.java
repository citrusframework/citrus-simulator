package org.citrusframework.simulator.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.model.MessageFilter;
import org.springframework.aop.framework.ProxyFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Factory for filters that provide default values.
 */
public class QueryFilterAdapterFactory {

    private SimulatorConfigurationProperties simulatorConfiguration;

    public QueryFilterAdapterFactory(SimulatorConfigurationProperties simulatorConfiguration) {
        this.simulatorConfiguration = simulatorConfiguration;
    }

    /**
     * Creates a filter that provides reasonable default values for the following certain filter attributes:<br>
     * 
     * <il>
     *   <li>fromDate</li>
     *   <li>toDate</li>
     *   <li>pageNumber</li>
     *   <li>pageSize</li>
     *   <li>directionInbound</li>
     *   <li>directionOutbound</li>
     * </il>
     * <br>
     *  
     * @param <T>
     * @param delegate
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends MessageFilter> T getQueryAdapter(T delegate) {
        ProxyFactory proxyFactory = new ProxyFactory(delegate);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new DefaultValuesQueryAdvice<T>(delegate));
        return (T)proxyFactory.getProxy();
    }

    private class DefaultValuesQueryAdvice<T extends MessageFilter> implements MethodInterceptor {

        private final T delegate;

        public DefaultValuesQueryAdvice(T delegate) {
            super();
            this.delegate = delegate;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if ("getFromDate".equals(invocation.getMethod().getName())) {
                return Optional.ofNullable(invocation.getMethod().invoke(delegate)).orElse(startOfDay());
            } else if ("getToDate".equals(invocation.getMethod().getName())) {
                return Optional.ofNullable(invocation.getMethod().invoke(delegate)).orElse(endOfDay());
            } else if ("getPageNumber".equals(invocation.getMethod().getName())) {
                return Optional.ofNullable(delegate.getPageNumber()).orElse(0);
            } else if ("getPageSize".equals(invocation.getMethod().getName())) {
                return Optional.ofNullable(delegate.getPageSize()).orElse(25);
            } else if ("getDirectionInbound".equals(invocation.getMethod().getName())) {
                return Optional.ofNullable(delegate.getDirectionInbound()).orElse(true);
            } else if ("getDirectionOutbound".equals(invocation.getMethod().getName())) {
                return Optional.ofNullable(delegate.getDirectionOutbound()).orElse(true);
            }

            return invocation.getMethod().invoke(delegate, invocation.getArguments());
        }

        private Instant startOfDay() {
            return LocalDate.now().atStartOfDay().minusDays(simulatorConfiguration.getFilterStartDayShift()).toInstant(ZoneOffset.UTC);
        }

        private Instant endOfDay() {
            return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        }
    }
}

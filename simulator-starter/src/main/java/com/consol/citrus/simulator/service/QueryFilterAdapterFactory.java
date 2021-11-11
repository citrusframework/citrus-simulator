package com.consol.citrus.simulator.service;

import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.model.MessageFilter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;


public class QueryFilterAdapterFactory {

    private SimulatorConfigurationProperties simulatorConfiguration;

    public QueryFilterAdapterFactory(SimulatorConfigurationProperties simulatorConfiguration) {
        super();
        this.simulatorConfiguration = simulatorConfiguration;
    }
    
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
        
        private Date startOfDay() {
            return Date.from(LocalDate.now().atStartOfDay().plusDays(-simulatorConfiguration.getFilterStartDayShift())
                    .toInstant(ZoneOffset.UTC));
        }

        private Date endOfDay() {
            return Date.from(LocalDate.now().plusDays(simulatorConfiguration.getFilterEndDayShift()).atStartOfDay()
                    .toInstant(ZoneOffset.UTC));
        }
    }
}
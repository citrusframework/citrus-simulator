/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.simulator;

import com.consol.citrus.simulator.servlet.SimulatorStatusServlet;
import com.consol.citrus.simulator.servlet.StaticResourceServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * This class replaces the "old" web.xml and is automatically scanned at the application startup
 */
public class WebAppInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/citrus-servlet-context.xml");

        ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet("citrus", new MessageDispatcherServlet());
        dispatcherServlet.setLoadOnStartup(1);
        dispatcherServlet.addMapping("/simulator");
        dispatcherServlet.addMapping("/simulator/*");
        dispatcherServlet.setInitParameter("contextConfigLocation", "");

        ServletRegistration.Dynamic statusServlet = servletContext.addServlet("status", new SimulatorStatusServlet());
        statusServlet.setLoadOnStartup(1000);
        statusServlet.addMapping("/status");
        statusServlet.addMapping("/status/*");

        ServletRegistration.Dynamic resourceServlet = servletContext.addServlet("resource", new StaticResourceServlet());
        resourceServlet.setLoadOnStartup(1000);
        resourceServlet.addMapping("/about");

        servletContext.addListener(new ContextLoaderListener(appContext));

        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        FilterRegistration.Dynamic filter = servletContext.addFilter("encoding-filter", encodingFilter);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}

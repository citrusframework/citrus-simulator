/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator.config;

import com.consol.citrus.util.FileUtils;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(WebMvcConfiguration.class);

    @Bean
    public ViewResolver getViewResolver() {
        HandlebarsViewResolver resolver = new HandlebarsViewResolver() {
            @Override
            public View resolveViewName(String viewName, Locale locale) throws Exception {
                if (viewName.endsWith(getSuffix())) {
                    return super.resolveViewName(viewName.substring(0, viewName.length() - getSuffix().length()), locale);
                } else {
                    return super.resolveViewName(viewName, locale);
                }
            }
        };
        resolver.setPrefix("classpath:/templates");
        resolver.setSuffix(".html");

        resolver.setValueResolvers(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE, new ValueResolver() {
            @Override
            public Object resolve(Object context, String name) {
                if (name.equals("version")) {
                    try {
                        return FileUtils.readToString(new ClassPathResource("static/version.txt"));
                    } catch (IOException e) {
                        LOG.warn("Unable to read version information", e);
                    }
                }

                return UNRESOLVED;
            }

            @Override
            public Object resolve(Object context) {
                return UNRESOLVED;
            }

            @Override
            public Set<Map.Entry<String, Object>> propertySet(Object context) {
                return Collections.emptySet();
            }
        });

        return resolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
        registry.addViewController("/about").setViewName("about");
    }
}

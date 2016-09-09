package com.consol.citrus.simulator.config;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Locale;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

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
        return resolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
        registry.addViewController("/about").setViewName("about");
    }
}

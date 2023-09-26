package org.citrusframework.simulator.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Note that this class may not be placed into the root package {@code org.citrusframework.simulator}. If done so, the
 * {@link SpringBootApplication} annotation would scan the classpath given the package and that would defy the sense
 * of all {@link org.springframework.boot.autoconfigure.AutoConfiguration}.
 */
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

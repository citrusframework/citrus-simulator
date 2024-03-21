package org.citrusframework.simulator.junit;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.citrusframework.simulator.junit.CitrusSimulatorContext.Resolver;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
@ExtendWith({CitrusSimulatorExtension.class, Resolver.class})
public @interface TestWithCitrusSimulator {

    /**
     * Whether to exclude the {@link org.citrusframework.simulator.SimulatorAutoConfiguration} from being registered
     * within the main application thread or not. Note that you would start two simulators if, one in the extension
     * thread and one in the main thread if the value of this property was {@code false}. That may alter your
     * application behaviour in an unexpected way.
     */
    boolean disableSimulatorForMainThread() default true;

    /**
     * A list of packages that contain simulator scenarios; These will be registered upon starting the extension.
     */
    String[] scenarioPackages() default {};

    /**
     * A list of properties into which the fully qualified URL of the Citrus Simulator (hostname and random port) will
     * be injected.
     */
    String[] urlProperties() default {};
}

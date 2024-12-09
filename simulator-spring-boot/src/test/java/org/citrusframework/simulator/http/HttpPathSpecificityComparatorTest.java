package org.citrusframework.simulator.http;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpPathSpecificityComparatorTest {

    private final HttpPathSpecificityComparator comparator = new HttpPathSpecificityComparator();

    static Stream<Object[]> pathSpecificityData() {
        return Stream.of(
            new Object[]{createMockScenario("/path/to/resource/{id}"), createMockScenario("/path/to/resource/a"), 1},
            new Object[]{createMockScenario("/path/to/resource"), createMockScenario("/path/to"), -1},
            new Object[]{createMockScenario("/path/to"), createMockScenario("/path/to/resource"), 1},
            new Object[]{createMockScenario("/path/{variable}/resource"), createMockScenario("/path/to/resource"), 1},
            new Object[]{createMockScenario("/path/to/resource"), createMockScenario("/path/{variable}/resource"), -1},
            new Object[]{createMockScenario("/path/to/resourceA"), createMockScenario("/path/to/resourceB"), -1},
            new Object[]{createMockScenario("/path/to/resource"), createMockScenario("/path/to/resource"), 0},
            new Object[]{createMockScenario(null), createMockScenario("/path/to/resource"), 1},
            new Object[]{createMockScenario("/path/to/resource"), createMockScenario(null), -1},
            new Object[]{createMockScenario(null), createMockScenario(null), 0}
        );
    }

    @DisplayName("Should correctly compare HttpScenarios by path specificity")
    @ParameterizedTest(name = "{index} => scenario1={0}, scenario2={1}, expected={2}")
    @MethodSource("pathSpecificityData")
    void testPathSpecificity(HttpScenario scenario1, HttpScenario scenario2, int expected) {
        int result = comparator.compare(scenario1, scenario2);

        Assertions.assertThat(result)
            .withFailMessage("Expected comparison result %d for paths %s and %s", expected,
                scenario1.getPath(),
                scenario2.getPath())
            .isEqualTo(expected);
    }

    // Helper method to create a mocked HttpScenario
    private static HttpScenario createMockScenario(String path) {
        HttpScenario mockScenario = Mockito.mock(HttpScenario.class);
        Mockito.when(mockScenario.getPath()).thenReturn(path);
        return mockScenario;
    }
}

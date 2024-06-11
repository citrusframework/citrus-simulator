package org.citrusframework.simulator.http;

import jakarta.annotation.Nonnull;
import java.util.Comparator;

/**
     * A comparator for HttpScenario objects that orders them based on the specificity
     * of their paths. Paths that are more specific (having more segments and fewer
     * variables or wildcards) are given higher priority in the ordering than less
     * specific paths. This follows conventions used in REST API routing and matching.
     */
    public class HttpPathSpecificityComparator implements
    Comparator<HttpScenario> {

        @Override
        public int compare(@Nonnull HttpScenario scenario1, @Nonnull HttpScenario scenario2) {

            Integer nullCompareResult = compareNullScenarios(scenario1, scenario2);
            if (nullCompareResult != null) {
                return nullCompareResult;
            }

            String path1 = scenario1.getPath();
            String path2 = scenario2.getPath();

            // Compare by segment count.
            int segmentCountComparison = compareSegmentCount(path1, path2);
            if (segmentCountComparison != 0) {
                return segmentCountComparison;
            }

            // Compare by variable presence in segments.
            int variableComparison = compareVariablesInSegments(path1, path2);
            if (variableComparison != 0) {
                return variableComparison;
            }

            // Finally, compare literally.
            return path1.compareTo(path2);
        }

        private Integer compareNullScenarios(HttpScenario scenario1, HttpScenario scenario2) {
            boolean path1IsNull = scenario1 == null || scenario1.getPath() == null;
            boolean path2IsNull = scenario2 == null || scenario2.getPath() == null;

            if (path1IsNull && path2IsNull) {
                return 0;
            } else if (path1IsNull) {
                return 1;
            } else if (path2IsNull) {
                return -1;
            }

            // Neither scenario nor path is null; defer comparison to further processing
            return null;
        }

        private int compareSegmentCount(String path1, String path2) {
            int segmentCount1 = path1.split("/+", -1).length;
            int segmentCount2 = path2.split("/+", -1).length;
            return Integer.compare(segmentCount2, segmentCount1);
        }

        private int compareVariablesInSegments(String path1, String path2) {
            String[] segments1 = path1.split("/+", -1);
            String[] segments2 = path2.split("/+", -1);

            for (int i = 0; i < segments1.length; i++) {
                boolean isVariable1 = isVariableSegment(segments1[i]);
                boolean isVariable2 = isVariableSegment(segments2[i]);

                if (isVariable1 && !isVariable2) {
                    return 1; // Path1 is less specific than Path2.
                } else if (!isVariable1 && isVariable2) {
                    return -1; // Path1 is more specific than Path2.
                }
            }
            return 0;
        }

    private boolean isVariableSegment(String segment) {
        return segment.startsWith("{") || segment.contains("*");
    }
    }

package org.citrusframework.simulator.ui.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ServletUtils {

    private static final String CONTEXT_PATH_PATTERN_STRING = "(.*?)(/\\*\\*?(/\\*)?)?$";
    private static final Pattern CONTEXT_PATH_PATTERN = Pattern.compile(CONTEXT_PATH_PATTERN_STRING);

    private ServletUtils() {
        // Static utility class
    }

    static CharSequence extractContextPath(CharSequence urlMapping) {
        Matcher matcher = CONTEXT_PATH_PATTERN.matcher(urlMapping);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return urlMapping;
    }
}

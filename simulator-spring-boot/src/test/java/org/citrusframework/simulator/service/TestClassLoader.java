/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class TestClassLoader {

    /**
     * Loads a Java class file from src/test/java as a string.
     *
     * @param packageName The package name with dots (e.g., "com.example.test")
     * @param className   The class name including .java extension (e.g., "MyTest.java")
     * @return The contents of the class file as a string
     * @throws IOException If the file cannot be read
     */
    static String loadTestClass(String packageName, String className) throws IOException {
        // Convert package name to path format
        String packagePath = packageName.replace('.', '/');

        // Build the full path
        Path classPath = Paths.get(
            "src/test/java",
            packagePath,
            className
        );

        // Read and return the file contents
        return Files.readString(classPath, StandardCharsets.UTF_8);
    }

    /**
     * Alternative method that takes the full class name
     *
     * @param fullClassName The fully qualified class name (e.g., "com.example.test.MyTest")
     * @return The contents of the class file as a string
     * @throws IOException If the file cannot be read
     */
    static String loadTestClass(String fullClassName) throws IOException {
        // Split the full class name into package and class
        int lastDot = fullClassName.lastIndexOf('.');
        String packageName = fullClassName.substring(0, lastDot);
        String className = fullClassName.substring(lastDot + 1) + ".java";

        return loadTestClass(packageName, className);
    }
}

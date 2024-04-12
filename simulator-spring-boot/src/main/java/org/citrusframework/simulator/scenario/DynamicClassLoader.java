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

package org.citrusframework.simulator.scenario;

import lombok.Getter;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.tools.ToolProvider.getSystemJavaCompiler;

public class DynamicClassLoader {

    private static final JavaCompiler JAVA_COMPILER = getSystemJavaCompiler();
    private static final StandardJavaFileManager STD_FILE_MANAGER = JAVA_COMPILER.getStandardFileManager(null, null, null);

    public static <T> Class<T> compileAndLoad(String className, String sourceCodeInText) throws Exception {
        JavaFileObject source = new InMemoryJavaFileObject(className, sourceCodeInText);
        Iterable<? extends JavaFileObject> compilationUnits = asList(source);

        // Prepare the in-memory file manager
        var inMemoryJavaFileManager = new InMemoryJavaFileManager(STD_FILE_MANAGER);

        // Compile the source code
        JavaCompiler.CompilationTask task = JAVA_COMPILER.getTask(null, inMemoryJavaFileManager, null, null, null, compilationUnits);
        if (task.call()) {
            // Load the class from the byte code stored in memory
            ClassLoader inMemoryClassLoader = new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    for (var byteCode : inMemoryJavaFileManager.getByteCodes()) {
                        if (getSimpleClassName(byteCode).equals(name)) {
                            byte[] bytes = byteCode.getByteCode();
                            return defineClass(getFullyQualifiedName(byteCode), bytes, 0, bytes.length);
                        }
                    }

                    return super.findClass(name);
                }

                private static String getFullyQualifiedName(InMemoryByteCode inMemoryByteCode) {
                    return inMemoryByteCode.getName().replaceAll("/", ".").replace(JavaFileObject.Kind.CLASS.extension, "").substring(1);
                }

                private static String getSimpleClassName(InMemoryByteCode inMemoryByteCode) {
                    var parts = inMemoryByteCode.getName().split("/");
                    return parts[parts.length - 1].replace(JavaFileObject.Kind.CLASS.extension, "");
                }
            };

            return (Class<T>) inMemoryClassLoader.loadClass(className);
        } else {
            throw new ClassNotFoundException("Class " + className + " not compiled");
        }
    }

    @Getter
    private static class InMemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> implements JavaFileManager {

        private final List<InMemoryByteCode> byteCodes = new ArrayList<>();

        public InMemoryJavaFileManager(StandardJavaFileManager stdFileManager) {
            super(stdFileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            InMemoryByteCode byteCode = new InMemoryByteCode(className);
            byteCodes.add(byteCode);
            return byteCode;
        }

    }

    private static class InMemoryJavaFileObject extends SimpleJavaFileObject {

        private final String content;

        public InMemoryJavaFileObject(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    @Getter
    private static class InMemoryByteCode extends SimpleJavaFileObject {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        public InMemoryByteCode(String className) {
            super(URI.create("byte:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        }

        public byte[] getByteCode() {
            return outputStream.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }
    }
}

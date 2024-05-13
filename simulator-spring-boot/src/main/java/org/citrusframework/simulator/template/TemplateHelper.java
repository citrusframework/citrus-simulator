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

package org.citrusframework.simulator.template;

import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.spi.Resource;
import org.citrusframework.spi.Resources;
import org.citrusframework.util.FileUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for loading templates from the classpath, in particular XML and JSON templates.
 */
public class TemplateHelper {

    private final String basePath;
    private final Charset charset;

    private TemplateHelper(String basePath, Charset charset) {
        this.basePath = adaptBasePath(basePath);
        this.charset = charset;
    }

    /**
     * Creates a new {@link TemplateHelper}
     *
     * @param basePath the base path from which the templates should be loaded
     * @param charset  the charset to read the templates with
     * @return the new instance
     */
    public static TemplateHelper instance(String basePath, Charset charset) {
        return new TemplateHelper(basePath, charset);
    }

    /**
     * Creates a new {@link TemplateHelper} with the UTF_8 {@link Charset}
     *
     * @param basePath the base path from which the templates should be loaded
     * @return the new instance
     */
    public static TemplateHelper instance(String basePath) {
        return instance(basePath, StandardCharsets.UTF_8);
    }

    private static String adaptBasePath(String basePath) {
        return StringUtils.endsWithIgnoreCase(basePath, "/") ? basePath : basePath + "/";
    }

    /**
     * Locates a message template using the supplied {@code templatePath} returning the contents as a string. Uses file
     * extension ".xml".
     *
     * @param templatePath the message template name.
     * @return the contents as a string
     */
    public String getXmlMessageTemplate(String templatePath) {
        return getMessageTemplate(templatePath, templatePath.endsWith(".xml") ? "" : "xml");
    }

    /**
     * Locates a message template using the supplied {@code templatePath} returning the contents as a string. Uses file
     * extension ".json".
     *
     * @param templatePath the message template name.
     * @return the contents as a string
     */
    public String getJsonMessageTemplate(String templatePath) {
        return getMessageTemplate(templatePath, templatePath.endsWith(".json") ? "" : "json");
    }

    /**
     * Locates a message template using the supplied {@code templatePath} returning the contents as a string. Uses given
     * file extension.
     *
     * @param templatePath      the message template name.
     * @param templateExtension template file extension.
     * @return the contents as a string
     */
    public String getMessageTemplate(String templatePath, String templateExtension) {
        try {
            return FileUtils.readToString(this.getFileResource(templatePath, templateExtension), charset);
        } catch (IOException e) {
            throw new CitrusRuntimeException(String.format("Error reading template: %s", templatePath), e);
        }
    }

    /**
     * Gets a classpath file resource from base template package.
     *
     * @param resourcePath      the relative path to the resource, including the resource name
     * @param resourceExtension the resource extension (e.g. '.xml')
     * @return the classpath resource
     */
    public Resource getFileResource(String resourcePath, String resourceExtension) {
        String adaptedFileExtension = resourceExtension;
        if (StringUtils.hasLength(resourceExtension) && !StringUtils.startsWithIgnoreCase(resourceExtension, ".")) {
            adaptedFileExtension = "." + resourceExtension;
        }
        return new Resources.ClasspathResource(basePath + resourcePath + adaptedFileExtension);
    }

    public String getBasePath() {
        return basePath;
    }

    public Charset getCharset() {
        return charset;
    }
}

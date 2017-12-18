package com.consol.citrus.simulator.template;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.FileUtils;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Helper class for loading templates from the classpath, in particular XML and JSON templates.
 */
@Getter
public class TemplateHelper {

    private final String basePath;
    private final Charset charset;

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

    private TemplateHelper(String basePath, Charset charset) {
        this.basePath = adaptBasePath(basePath);
        this.charset = charset;
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
        return new ClassPathResource(basePath + resourcePath + adaptedFileExtension);
    }

    private static String adaptBasePath(String basePath) {
        return StringUtils.endsWithIgnoreCase(basePath, "/") ? basePath : basePath + "/";
    }
}

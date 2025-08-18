package org.socialmedia.app.config.sanitizers;

import jakarta.annotation.Nullable;
import org.owasp.html.HtmlChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// O <Void> indica que não passaremos um contexto customizado.
public class SanitizerListener implements HtmlChangeListener<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SanitizerListener.class);

    @Override
    public void discardedTag(@Nullable Void context, String elementName) {
        LOGGER.warn("Sanitizer discarded tag: <{}>", elementName);
    }

    @Override
    public void discardedAttributes(@Nullable Void context, String tagName, String... attributeNames) {
        LOGGER.warn("Sanitizer discarded attributes [{}] from tag <{}>", String.join(", ", attributeNames), tagName);
    }
}
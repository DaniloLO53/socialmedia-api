package org.socialmedia.app.config.sanitizers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class SanitizingJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String originalValue = p.getValueAsString();
        return HtmlSanitizerConfig.sanitize(originalValue);
    }
}
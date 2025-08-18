package org.socialmedia.app.payload.nodes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.socialmedia.app.config.sanitizers.SanitizingJsonDeserializer;

public record CreateSubNodeRequest(
        @JsonDeserialize(using = SanitizingJsonDeserializer.class) @NotBlank String name,
        @JsonDeserialize(using = SanitizingJsonDeserializer.class) @NotBlank String description
) {}

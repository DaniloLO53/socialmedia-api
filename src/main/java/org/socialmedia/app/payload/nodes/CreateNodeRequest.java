package org.socialmedia.app.payload.nodes;

import jakarta.validation.constraints.NotBlank;

public record CreateNodeRequest(
        @NotBlank String name,
        @NotBlank String description
) {}

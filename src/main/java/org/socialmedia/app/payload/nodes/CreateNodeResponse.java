package org.socialmedia.app.payload.nodes;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateNodeResponse(
        UUID id,
        @NotBlank String name,
        @NotBlank String description
) {}

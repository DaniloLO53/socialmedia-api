package org.socialmedia.app.payload.moderators;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddModeratorRequest(
        @NotNull UUID userId
) {}

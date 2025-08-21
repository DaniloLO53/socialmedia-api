package org.socialmedia.app.payload.moderators;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetNodeModeratorsResponse {
    @NotNull UUID id;
    @NotNull String firstName;
    @NotNull String lastName;
    @NotNull String username;
}

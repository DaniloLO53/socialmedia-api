package org.socialmedia.app.payload.nodes;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateRootNodeResponse {
    UUID id;
    @NotBlank String name;
    @NotBlank String description;
}

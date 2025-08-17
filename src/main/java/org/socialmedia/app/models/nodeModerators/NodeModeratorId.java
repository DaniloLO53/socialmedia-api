package org.socialmedia.app.models.nodeModerators;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable // Indica que esta classe pode ser embutida em outra entidade
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Essencial para chaves compostas
public class NodeModeratorId implements Serializable {
    private UUID userId;
    private UUID nodeId;
}

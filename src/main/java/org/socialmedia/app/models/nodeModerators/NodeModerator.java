package org.socialmedia.app.models.nodeModerators;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.users.User;

import java.time.OffsetDateTime;

@Entity
@Table(name = "node_moderators")
@Getter
@Setter
public class NodeModerator {

    @EmbeddedId
    private NodeModeratorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("nodeId") // Mapeia a propriedade 'nodeId' da nossa @EmbeddedId
    @JoinColumn(name = "node_id")
    private Node node;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime assignedAt;
}

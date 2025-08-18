package org.socialmedia.app.models.nodes;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.nodeModerators.NodeModerator;
import org.socialmedia.app.models.threads.Thread;
import org.socialmedia.app.models.users.User;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "nodes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"creator", "parentNode", "threads"}) // Evita loops em logs
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Size(max = 21, message = "Nome do node deve ter no máximo 21 caracteres")
    @Column(length = 21, nullable = false)
    private String name;

    @NotBlank
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_node_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Node parentNode;

    @OneToMany(mappedBy = "parentNode", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Node> childNodes = new HashSet<>();

    @OneToMany(mappedBy = "node", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = false)
    // Set é mais performático para adicionar / remover
    private Set<Thread> threads = new HashSet<>();

    @OneToMany(
            mappedBy = "node",
            cascade = { CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true
    )
    private Set<NodeModerator> moderators = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedNodes") // "subscribedNodes" é o nome do campo na entidade User
    private Set<User> subscribers = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    public void addChildNode(Node child) {
        this.childNodes.add(child);
        child.setParentNode(this);
    }

    public void removeChildNode(Node child) {
        this.childNodes.remove(child);
        child.setParentNode(null);
    }

    public void addThread(Thread thread) {
        this.threads.add(thread);
        thread.setNode(this);
    }

    public void removeThread(Thread thread) {
        this.threads.remove(thread);
        thread.setNode(null);
    }

    public void addModerator(NodeModerator moderator) {
        this.moderators.add(moderator);
        moderator.setNode(this);
    }

    public void removeModerator(NodeModerator moderator) {
        this.moderators.remove(moderator);
        moderator.setNode(null);
    }
}

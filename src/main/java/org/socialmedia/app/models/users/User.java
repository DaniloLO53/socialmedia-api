package org.socialmedia.app.models.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.nodeModerators.NodeModerator;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.replies.Reply;
import org.socialmedia.app.models.threads.Thread;
import org.socialmedia.app.models.votes.Vote;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {""}) // Evita loops em logs
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @NotBlank
    @Column(name = "username", length = 100, nullable = false)
    @Size(max = 100)
    private String username;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "status_message")
    private String statusMessage;

    @NotNull
    @Enumerated(EnumType.STRING) // Armazena o nome do enum ("ONLINE", "OFFLINE") no banco
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @OneToMany(mappedBy = "creator", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = false)
    // Set é mais performático para adicionar / remover
    private Set<Node> nodes;

    @OneToMany(mappedBy = "creator", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = false)
    // Set é mais performático para adicionar / remover
    private Set<Thread> threads;

    @OneToMany(mappedBy = "creator", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = false)
    // Set é mais performático para adicionar / remover
    private Set<Reply> replies;

    @OneToMany(
            mappedBy = "user", // 'user' é o nome do campo em NodeModerator
            cascade = { CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true
    )
    // Set para reforçar que um usuário só pode ser moderador de um node uma única vez
    private Set<NodeModerator> moderatedNodes;

    // Set para reforçar que um usuário só pode ser moderador de um node uma única vez
    @OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Vote> votes;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "node_subscriptions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "node_id")
    )
    private Set<Node> subscribedNodes = new HashSet<>();

    // Métodos auxiliares para gerenciar o relacionamento (Boa Prática)
    public void addNode(Node node) {
        this.nodes.add(node);
        node.setCreator(this);
    }

    public void removeNode(Node node) {
        this.nodes.remove(node);
        node.setCreator(null);
    }

    public void addThread(Thread thread) {
        this.threads.add(thread);
        thread.setCreator(this);
    }

    public void removeThread(Thread thread) {
        this.threads.remove(thread);
        thread.setCreator(null);
    }

    public void addReply(Reply reply) {
        this.replies.add(reply);
        reply.setCreator(this);
    }

    public void removeReply(Reply reply) {
        this.replies.remove(reply);
        reply.setCreator(null);
    }

    public void addModeratorRole(NodeModerator moderatorRole) {
        this.moderatedNodes.add(moderatorRole);
        moderatorRole.setUser(this);
    }

    public void removeModeratorRole(NodeModerator moderatorRole) {
        this.moderatedNodes.remove(moderatorRole);
        moderatorRole.setUser(null);
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
        vote.setUser(this);
    }

    public void removeVote(Vote vote) {
        this.votes.remove(vote);
        vote.setUser(null);
    }

    public void subscribeToNode(Node node) {
        this.subscribedNodes.add(node);
        node.getSubscribers().add(this);
    }

    public void unsubscribeFromNode(Node node) {
        this.subscribedNodes.remove(node);
        node.getSubscribers().remove(this);
    }
}
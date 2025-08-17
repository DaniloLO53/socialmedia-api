package org.socialmedia.app.models.threads;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.replies.Reply;
import org.socialmedia.app.models.tags.Tag;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.models.votes.Vote;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "threads")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"creator", "node"}) // Evita loops em logs
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Node node;

    @OneToMany(mappedBy = "thread", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = false)
    // Set é mais performático para adicionar / remover
    private Set<Reply> replies;

    // Set para reforçar que um usuário só pode ser moderador de um node uma única vez
    @OneToMany(mappedBy = "thread", cascade = { CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Vote> votes;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "thread_tags", // O nome da sua tabela de junção
        joinColumns = @JoinColumn(name = "thread_id"), // A FK para esta entidade (Thread)
        inverseJoinColumns = @JoinColumn(name = "tag_id") // A FK para a outra entidade (Tag)
    )
    private Set<Tag> tags = new HashSet<>();

    public void addReply(Reply reply) {
        this.replies.add(reply);
        reply.setThread(this);
    }

    public void removeReply(Reply reply) {
        this.replies.remove(reply);
        reply.setThread(null);
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
        vote.setThread(this);
    }

    public void removeVote(Vote vote) {
        this.votes.remove(vote);
        vote.setThread(null);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getThreads().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getThreads().remove(this);
    }
}

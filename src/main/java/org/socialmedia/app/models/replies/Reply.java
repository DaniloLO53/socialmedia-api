package org.socialmedia.app.models.replies;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.threads.Thread;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.models.votes.Vote;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "replies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {""}) // Evita loops em logs
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Thread thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Reply parentReply;

    @OneToMany(
            mappedBy = "parentReply",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Reply> childReplies = new HashSet<>();

    // Set para reforçar que um usuário só pode ser moderador de um node uma única vez
    @OneToMany(mappedBy = "reply", cascade = { CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Vote> votes;

    public void addChildReply(Reply child) {
        this.childReplies.add(child);
        child.setParentReply(this);
    }

    public void removeChildReply(Reply child) {
        this.childReplies.remove(child);
        child.setParentReply(null);
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
        vote.setReply(this);
    }

    public void removeVote(Vote vote) {
        this.votes.remove(vote);
        vote.setReply(null);
    }
}

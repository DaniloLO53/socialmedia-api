package org.socialmedia.app.models.votes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.replies.Reply;
import org.socialmedia.app.models.replies.VoteDirection;
import org.socialmedia.app.models.threads.Thread;
import org.socialmedia.app.models.users.User;

import java.util.UUID;

@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(name = "unique_user_vote_on_thread", columnNames = {"user_id", "thread_id"}),
    @UniqueConstraint(name = "unique_user_vote_on_reply", columnNames = {"user_id", "reply_id"})
})
@Check(constraints = "(thread_id IS NOT NULL AND reply_id IS NULL) OR (thread_id IS NULL AND reply_id IS NOT NULL)")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "thread", "reply"}) // Evita loops em logs
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Short direction;

    // Metodo auxiliar para usar o Enum de forma segura
    public void setDirection(VoteDirection voteDirection) {
        this.direction = (short) voteDirection.getValue();
    }
}


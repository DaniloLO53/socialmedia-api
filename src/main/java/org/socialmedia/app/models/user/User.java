package org.socialmedia.app.models.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.nodes.Node;

import java.time.OffsetDateTime;
import java.util.List;
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
    private List<Node> nodes;
}
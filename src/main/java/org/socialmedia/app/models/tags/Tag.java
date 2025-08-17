package org.socialmedia.app.models.tags;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.socialmedia.app.models.threads.Thread;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {""}) // Evita loops em logs
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Thread> threads = new HashSet<>();
}


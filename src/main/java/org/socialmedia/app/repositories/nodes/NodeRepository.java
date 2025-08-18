package org.socialmedia.app.repositories.nodes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NodeRepository extends JpaRepository<Node, UUID> {
    boolean existsByNameIgnoreCaseAndParentNode(String name, Node parentNode);
    boolean existsByNameIgnoreCaseAndParentNodeIsNull(String name);
    boolean existsByCreatorAndCreatedAtAfter(User creator, OffsetDateTime sinceDate);
    Optional<Node> findFirstById(UUID id);
}

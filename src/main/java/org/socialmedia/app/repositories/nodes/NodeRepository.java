package org.socialmedia.app.repositories.nodes;

import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface NodeRepository extends JpaRepository<Node, UUID> {
    boolean existsByNameIgnoreCaseAndParentNodeIsNull(String name);
    boolean existsByCreatorAndCreatedAtAfter(User creator, OffsetDateTime sinceDate);
}

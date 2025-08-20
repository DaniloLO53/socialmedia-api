package org.socialmedia.app.repositories.nodeModerators;

import org.socialmedia.app.models.nodeModerators.NodeModerator;
import org.socialmedia.app.models.nodeModerators.NodeModeratorId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeModeratorRepository extends JpaRepository<NodeModerator, NodeModeratorId> {
}

package org.socialmedia.app.services.nodes;

import jakarta.validation.Valid;
import org.socialmedia.app.payload.moderators.AddModeratorRequest;
import org.socialmedia.app.payload.moderators.AddModeratorResponse;
import org.socialmedia.app.payload.moderators.GetNodeModeratorsResponse;
import org.socialmedia.app.payload.nodes.CreateRootNodeRequest;
import org.socialmedia.app.payload.nodes.CreateRootNodeResponse;
import org.socialmedia.app.payload.nodes.CreateSubNodeRequest;
import org.socialmedia.app.payload.nodes.CreateSubNodeResponse;
import org.socialmedia.app.security.services.UserDetailsImpl;

import java.util.List;
import java.util.UUID;

public interface NodeService {
    CreateRootNodeResponse createRootNode(UserDetailsImpl userDetails, CreateRootNodeRequest payload);
    CreateSubNodeResponse createSubNode(UserDetailsImpl userDetails, UUID parentNodeId, CreateSubNodeRequest payload);
    AddModeratorResponse addModerator(UUID nodeId, AddModeratorRequest payload);
    List<GetNodeModeratorsResponse> getNodeModerators(UUID nodeId);
}

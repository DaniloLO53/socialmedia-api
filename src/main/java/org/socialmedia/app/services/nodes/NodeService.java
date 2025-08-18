package org.socialmedia.app.services.nodes;

import org.socialmedia.app.payload.nodes.CreateRootNodeRequest;
import org.socialmedia.app.payload.nodes.CreateRootNodeResponse;
import org.socialmedia.app.security.services.UserDetailsImpl;

public interface NodeService {
    CreateRootNodeResponse createRootNode(UserDetailsImpl userDetails, CreateRootNodeRequest payload);
}

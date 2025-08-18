package org.socialmedia.app.services.nodes;

import org.socialmedia.app.payload.nodes.CreateNodeRequest;
import org.socialmedia.app.payload.nodes.CreateNodeResponse;
import org.socialmedia.app.security.services.UserDetailsImpl;

public interface NodeService {
    CreateNodeResponse createNode(UserDetailsImpl userDetails, CreateNodeRequest payload);
}

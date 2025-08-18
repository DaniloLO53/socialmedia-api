package org.socialmedia.app.controllers;

import jakarta.validation.Valid;
import org.socialmedia.app.payload.nodes.CreateNodeRequest;
import org.socialmedia.app.payload.nodes.CreateNodeResponse;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.socialmedia.app.services.nodes.NodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NodeController {
    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping("/nodes")
    public ResponseEntity<CreateNodeResponse> createNode(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CreateNodeRequest payload
    ) {
        CreateNodeResponse node = nodeService.createNode(userDetails, payload);

        return ResponseEntity.status(HttpStatus.CREATED).body(node);
    }
}

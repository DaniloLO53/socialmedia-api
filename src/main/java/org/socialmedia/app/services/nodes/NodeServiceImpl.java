package org.socialmedia.app.services.nodes;

import org.modelmapper.ModelMapper;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.payload.nodes.CreateNodeRequest;
import org.socialmedia.app.payload.nodes.CreateNodeResponse;
import org.socialmedia.app.repositories.nodes.NodeRepository;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;

@Service
public class NodeServiceImpl implements NodeService {
    private final ModelMapper modelMapper;
    private final NodeRepository nodeRepository;

    public NodeServiceImpl(ModelMapper modelMapper, NodeRepository nodeRepository) {
        this.modelMapper = modelMapper;
        this.nodeRepository = nodeRepository;
    }

    @Override
    public CreateNodeResponse createNode(UserDetailsImpl userDetails, CreateNodeRequest payload) {
        Node newNode = new Node();

        newNode.setCreator(userDetails.getUser());
        newNode.setName(payload.name());
        newNode.setDescription(payload.description());

        Node savedNode = nodeRepository.save(newNode);

        return modelMapper.map(savedNode, CreateNodeResponse.class);
    }
}

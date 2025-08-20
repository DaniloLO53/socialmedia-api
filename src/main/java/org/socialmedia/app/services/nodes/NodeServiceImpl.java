package org.socialmedia.app.services.nodes;

import org.modelmapper.ModelMapper;
import org.socialmedia.app.exceptions.ConflictException;
import org.socialmedia.app.exceptions.ResourceNotFoundException;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.payload.nodes.CreateRootNodeRequest;
import org.socialmedia.app.payload.nodes.CreateRootNodeResponse;
import org.socialmedia.app.payload.nodes.CreateSubNodeRequest;
import org.socialmedia.app.payload.nodes.CreateSubNodeResponse;
import org.socialmedia.app.repositories.nodes.NodeRepository;
import org.socialmedia.app.repositories.users.UserRepository;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.socialmedia.app.utils.SlugUtil;
import org.socialmedia.app.utils.TimeUtil;
import org.socialmedia.app.utils.UnicodeNormalizer;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class NodeServiceImpl implements NodeService {
    private final ModelMapper modelMapper;
    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;

    public NodeServiceImpl(ModelMapper modelMapper, NodeRepository nodeRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CreateRootNodeResponse createRootNode(UserDetailsImpl userDetails, CreateRootNodeRequest payload) {
        // Aqui, user está desanexado dos seus nodes devido à inicialização LAZY (a transação que buscou o User já terminou)
        User detachedCreator = userDetails.getUser();

        // Aqui, uma nova transação é aberta
        User creator = userRepository.findFirstById(detachedCreator.getId());
        String normalizedNodeName = SlugUtil.validateAndSanitizeName(payload.name());

        if (nodeRepository.existsByNameIgnoreCaseAndParentNodeIsNull(normalizedNodeName)) {
            throw new ConflictException("Já existe um node raíz com esse nome. Por favor, escolha outro nome.");
        }

        if (verifyUserReputation(creator)) {
            throw new ConflictException("Usuário sem reputação suficiente para criar novo node.");
        }

//        if (hasUserCreatedNodeRecently(creator, 1)) {
//            throw new ConflictException("Usuário deve esperar 1 dia desde o último node criado.");
//        }

        Node newNode = new Node();
        newNode.setName(normalizedNodeName);
        newNode.setDescription(UnicodeNormalizer.normalizeForComparison(payload.description()));

        creator.addNode(newNode);

        Node savedNode = nodeRepository.save(newNode);

        return modelMapper.map(savedNode, CreateRootNodeResponse.class);
    }

    @Override
    public CreateSubNodeResponse createSubNode(UserDetailsImpl userDetails, UUID parentNodeId, CreateSubNodeRequest payload) {
        User detachedCreator = userDetails.getUser();

        // Aqui, uma nova transação é aberta
        User creator = userRepository.findFirstById(detachedCreator.getId());
        String normalizedNodeName = SlugUtil.validateAndSanitizeName(payload.name());

        if (verifyUserReputation(creator)) {
            throw new ConflictException("Usuário sem reputação suficiente para criar novo node.");
        }

        if (hasUserCreatedNodeRecently(creator, 1)) {
//            throw new ConflictException("Usuário deve esperar 1 dia desde o último node criado.");
        }

        Node newNode = new Node();
        newNode.setName(normalizedNodeName);
        newNode.setDescription(UnicodeNormalizer.normalizeForComparison(payload.description()));

        Node parentNode = nodeRepository
                .findFirstById(parentNodeId).orElseThrow(() -> new ResourceNotFoundException("Node", "id", parentNodeId.toString()));

        if (nodeRepository.existsByNameIgnoreCaseAndParentNode(normalizedNodeName, parentNode)) {
            throw new ConflictException("Já existe um subnode com esse nome. Por favor, escolha outro nome.");
        }

        verifyNestedNodesDepthLimit(parentNode);

        parentNode.addChildNode(newNode);
        creator.addNode(newNode);

        Node savedNode = nodeRepository.save(newNode);

        return modelMapper.map(savedNode, CreateSubNodeResponse.class);
    }

    private void verifyNestedNodesDepthLimit(Node parentNode) {
        final int MAX_DEPTH = 7;
        int currentDepth = 0;
        Node ancestor = parentNode;
        while (ancestor != null) {
            currentDepth++;
            ancestor = ancestor.getParentNode();
            if (currentDepth > MAX_DEPTH) {
                break;
            }
        }

        if (currentDepth >= MAX_DEPTH) {
            throw new ConflictException(
                    "Não é possível criar um sub-node. A profundidade máxima de " + MAX_DEPTH + " níveis foi atingida."
            );
        }
    }

    // TODO: a reputação do usuário precisa ser uma estrutura mais robusta. Por enquanto, o tempo de conta é suficiente
    private Boolean verifyUserReputation(User user) {
        return TimeUtil.getDaysBetweenRoundedUp(user.getCreatedAt()) > 30;
    }

    private boolean hasUserCreatedNodeRecently(User user, long days) {
        OffsetDateTime sinceDate = OffsetDateTime.now().minusDays(days);

        return nodeRepository.existsByCreatorAndCreatedAtAfter(user, sinceDate);
    }
}

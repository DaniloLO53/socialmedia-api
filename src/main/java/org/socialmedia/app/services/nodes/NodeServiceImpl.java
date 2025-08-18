package org.socialmedia.app.services.nodes;

import org.modelmapper.ModelMapper;
import org.socialmedia.app.exceptions.ConflictException;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.payload.nodes.CreateRootNodeRequest;
import org.socialmedia.app.payload.nodes.CreateRootNodeResponse;
import org.socialmedia.app.repositories.nodes.NodeRepository;
import org.socialmedia.app.repositories.users.UserRepository;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.socialmedia.app.utils.SlugUtil;
import org.socialmedia.app.utils.TimeUtil;
import org.socialmedia.app.utils.UnicodeNormalizer;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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

        if (hasUserCreatedNodeRecently(creator, 1)) {
//            throw new ConflictException("Usuário deve esperar 1 dia desde o último node criado.");
        }

        Node newNode = new Node();
        newNode.setName(normalizedNodeName);
        newNode.setDescription(UnicodeNormalizer.normalizeForComparison(payload.description()));

        creator.addNode(newNode);

        Node savedNode = nodeRepository.save(newNode);

        return modelMapper.map(savedNode, CreateRootNodeResponse.class);
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

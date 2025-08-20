package org.socialmedia.app.security.services;

import org.socialmedia.app.exceptions.ForbiddenException;
import org.socialmedia.app.exceptions.ResourceNotFoundException;
import org.socialmedia.app.models.nodes.Node;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.repositories.nodes.NodeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("permissionService")
public class PermissionService {
    private final NodeRepository nodeRepository;

    public PermissionService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public void checkIsNodeCreator(Authentication authentication, UUID nodeId) {
        User currentUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node não encontrado."));

        boolean isCreator = node.getCreator() != null && node.getCreator().getId().equals(currentUser.getId());

        if (!isCreator) {
            throw new ForbiddenException("Acesso negado. Apenas o criador do node pode realizar esta ação.");
        }
    }

    public void checkCanModerateNode(Authentication authentication, UUID nodeId) {
        User currentUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node não encontrado."));

        boolean isCreator = node.getCreator() != null && node.getCreator().getId().equals(currentUser.getId());
        boolean isModerator = node.getModerators().stream()
                .anyMatch(mod -> mod.getUser().getId().equals(currentUser.getId()));

        if (!isCreator && !isModerator) {
            throw new ForbiddenException("Acesso negado. Apenas moderadores ou o criador do node podem realizar esta ação.");
        }
    }

    public void checkIsNodeSubscriber(Authentication authentication, UUID nodeId) {
        User currentUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

        // Esta busca pode ser otimizada para não carregar a entidade inteira
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node não encontrado."));

        boolean isSubscriber = node.getSubscribers().stream()
                .anyMatch(sub -> sub.getId().equals(currentUser.getId()));

        if (!isSubscriber) {
            throw new ForbiddenException("Acesso negado. Apenas inscritos no node podem realizar esta ação.");
        }
    }
}
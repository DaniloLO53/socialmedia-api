package org.socialmedia.app.security.services;

import org.socialmedia.app.exceptions.ResourceNotFoundException;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.repositories.users.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .findFirstByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));

        return new UserDetailsImpl(user.getId(), email, user.getPassword());
    }
}

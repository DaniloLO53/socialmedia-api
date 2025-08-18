package org.socialmedia.app.services.auth;

import org.socialmedia.app.exceptions.ConflictException;
import org.socialmedia.app.models.users.AccountStatus;
import org.socialmedia.app.models.users.User;
import org.socialmedia.app.payload.auth.SignInRequestPayload;
import org.socialmedia.app.payload.auth.SignUpRequestPayload;
import org.socialmedia.app.payload.auth.SignInResponsePayload;
import org.socialmedia.app.payload.auth.UserInfoPayload;
import org.socialmedia.app.repositories.users.UserRepository;
import org.socialmedia.app.security.jwt.JwtUtils;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AuthServiceImpl(ModelMapper modelMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public SignInResponsePayload signIn(SignInRequestPayload payload) {
        String email = payload.getEmail();
        String password = payload.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieFromUserDetails(userDetails);

        return new SignInResponsePayload(email, jwtCookie.toString());
    }

    @Override
    public void signUp(SignUpRequestPayload payload) {
        String email = payload.getEmail();
        String username = payload.getUsername();
        String password = payload.getPassword();
        String passwordConfirmation = payload.getPasswordConfirmation();

        if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
           throw new ConflictException("Usuário já cadastrado");
        }

        if (!password.equals(passwordConfirmation)) {
            throw new ConflictException("As senhas não são iguais");
        }

        String encodedPassword = passwordEncoder.encode(password);
        payload.setPassword(encodedPassword);

        User user = modelMapper.map(payload, User.class);

        user.setAccountStatus(AccountStatus.OFFLINE); // Mudar para implementação com sockets
        userRepository.save(user);
    }

    @Override
    public UserInfoPayload getUserInfoByUserDetails(UserDetailsImpl userDetails) {
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieFromUserDetails(userDetails);

        UUID userId = userDetails.getId();
        String email = userDetails.getUsername();
        String token = jwtUtils.getJwtTokenFromCookieString(jwtCookie.toString());

        return new UserInfoPayload(userId, email, token);
    }

    @Override
    public ResponseCookie getCleanJwtCookie() {
        return jwtUtils.getCleanJwtCookie();
    }
}

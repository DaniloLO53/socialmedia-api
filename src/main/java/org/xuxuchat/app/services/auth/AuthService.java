package org.xuxuchat.app.services.auth;

import jakarta.validation.Valid;
import org.xuxuchat.app.payload.auth.SignInRequestPayload;
import org.xuxuchat.app.payload.auth.SignUpRequestPayload;
import org.xuxuchat.app.payload.auth.SignInResponsePayload;
import org.xuxuchat.app.payload.auth.UserInfoPayload;
import org.xuxuchat.app.security.services.UserDetailsImpl;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    void signUp(SignUpRequestPayload payload);
    SignInResponsePayload signIn(@Valid SignInRequestPayload payload);
    UserInfoPayload getUserInfoByUserDetails(UserDetailsImpl userDetails);
    ResponseCookie getCleanJwtCookie();
}

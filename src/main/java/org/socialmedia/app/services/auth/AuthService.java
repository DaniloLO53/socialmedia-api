package org.socialmedia.app.services.auth;

import jakarta.validation.Valid;
import org.socialmedia.app.payload.auth.SignInRequestPayload;
import org.socialmedia.app.payload.auth.SignUpRequestPayload;
import org.socialmedia.app.payload.auth.SignInResponsePayload;
import org.socialmedia.app.payload.auth.UserInfoPayload;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    void signUp(SignUpRequestPayload payload);
    SignInResponsePayload signIn(@Valid SignInRequestPayload payload);
    UserInfoPayload getUserInfoByUserDetails(UserDetailsImpl userDetails);
    ResponseCookie getCleanJwtCookie();
}

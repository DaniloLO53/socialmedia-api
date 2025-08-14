package org.socialmedia.app.controllers;

import jakarta.validation.Valid;
import org.socialmedia.app.payload.auth.SignInRequestPayload;
import org.socialmedia.app.payload.auth.SignUpRequestPayload;
import org.socialmedia.app.payload.auth.SignInResponsePayload;
import org.socialmedia.app.payload.auth.UserInfoPayload;
import org.socialmedia.app.security.services.UserDetailsImpl;
import org.socialmedia.app.services.auth.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello, world");
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoPayload> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoPayload payload = authService.getUserInfoByUserDetails(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(payload);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestPayload payload) {
        authService.signUp(payload);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<SignInResponsePayload> signIn(@RequestBody @Valid SignInRequestPayload payload) {
        SignInResponsePayload response = authService.signIn(payload);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.jwtCookieString())
                .body(response);
    }

    @PatchMapping("/signout")
    public ResponseEntity<?> signOut() {
        ResponseCookie cleanJwtCookie = authService.getCleanJwtCookie();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .body(null);
    }
}

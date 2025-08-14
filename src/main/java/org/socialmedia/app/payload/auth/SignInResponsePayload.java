package org.socialmedia.app.payload.auth;

public record SignInResponsePayload (
    String email,
    String jwtCookieString
){}

package org.xuxuchat.app.payload.auth;

public record SignInResponsePayload (
    String email,
    String jwtCookieString
){}

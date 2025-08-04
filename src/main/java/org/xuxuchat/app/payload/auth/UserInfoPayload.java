package org.xuxuchat.app.payload.auth;

import java.util.UUID;

public record UserInfoPayload (
    UUID id,
    String email,
    String jwtCookieString
){}

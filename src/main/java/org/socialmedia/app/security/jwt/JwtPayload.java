package org.socialmedia.app.security.jwt;

import java.util.UUID;

public record JwtPayload(UUID id, String email) {
    public static class Builder {
        private UUID idField;
        private String emailField;

        public Builder id(UUID id) {
            this.idField = id;
            return this;
        }

        public Builder email(String email) {
            this.emailField = email;
            return this;
        }

        public JwtPayload build() {
            return new JwtPayload(idField, emailField);
        }
    }
}

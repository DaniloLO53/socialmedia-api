package org.xuxuchat.app.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.xuxuchat.app.exceptions.InvalidJwtException;
import org.xuxuchat.app.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private SecretKey jwtKey;
    private String jwtKeyId;
    private String jwtIssuer;
    private String jwtAudience;
    private String jwtCookieName;
    private Long jwtExpTime;

    public JwtUtils(
            @Value("${app.jwtKeyId}") String jwtKeyId,
            @Value("${app.jwtIssuer}") String jwtIssuer,
            @Value("${app.jwtAudience}") String jwtAudience,
            @Value("${app.jwtCookieName}") String jwtCookieName,
            @Value("${app.jwtExpTime}") Long jwtExpTime,
            @Value("${app.jwtSecret}") String jwtKey
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtKey);
        this.jwtKey = Keys.hmacShaKeyFor(keyBytes);

        this.jwtKeyId = jwtKeyId;
        this.jwtAudience = jwtAudience;
        this.jwtCookieName = jwtCookieName;
        this.jwtExpTime = jwtExpTime;
        this.jwtIssuer = jwtIssuer;
    }

    public Claims validateAndParseClaims(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(jwtKey)
                    .requireIssuer(jwtIssuer)
                    .requireAudience(jwtAudience)
                    .build();

            return parser.parseSignedClaims(token).getPayload();

        } catch (ExpiredJwtException e) {
            throw new InvalidJwtException(String.format("JWT Token has expired: %s", e.getMessage()), e);

        } catch (UnsupportedJwtException e) {
            throw new InvalidJwtException(String.format("JWT Token is unsupported: %s", e.getMessage()), e);

        } catch (MalformedJwtException e) {
            throw new InvalidJwtException(String.format("Invalid JWT Token: %s", e.getMessage()), e);

        } catch (SignatureException e) {
            throw new InvalidJwtException(String.format("Invalid JWT signature: %s", e.getMessage()), e);

        } catch (IllegalArgumentException e) {
            throw new InvalidJwtException(String.format("JWT claims string is empty: %s", e.getMessage()), e);

        } catch (InvalidClaimException e) {
            throw new InvalidJwtException(String.format("Invalid JWT claim: %s", e.getMessage()), e);
        }
    }

    public String buildJwt(JwtPayload payload) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + jwtExpTime;
        Date expirationDate = new Date(expMillis);

        return Jwts.builder()
                .header()
                .keyId(jwtKeyId)
                .and()
                .subject(payload.email())
                .issuer(jwtIssuer) // (Opcional) Quem emitiu o token
                .issuedAt(now) // Data de emissão
                .audience().add(jwtAudience)
                .and()
                .expiration(expirationDate)

                .claim("id", payload.id())

                .signWith(jwtKey)

                .compact();
    }

    public ResponseCookie generateJwtCookieFromUserDetails(UserDetailsImpl userDetails) {
        JwtPayload payload = new JwtPayload.Builder()
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .build();
        String jwtToken = buildJwt(payload);

        // TODO: change to secure(true) and sameSite.NONE for production
        return ResponseCookie.from(jwtCookieName, jwtToken)
                .path("/api")
                .maxAge(jwtExpTime)
                .httpOnly(true) // avoids js access
                .secure(false)
                .sameSite(SameSite.LAX.attributeValue())
                .build();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);

        return cookie == null ? null : cookie.getValue();
    }

    public String getJwtTokenFromCookieString(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return null;
        }

        // 1. Divide a string do cabeçalho em partes baseadas no ';'
        String[] cookies = cookieHeader.split(";");
        String prefix = jwtCookieName + "=";

        // 2. Itera por cada parte para encontrar o cookie correto
        for (String cookie : cookies) {
            String trimmedCookie = cookie.trim(); // Remove espaços em branco
            if (trimmedCookie.startsWith(prefix)) {
                // 3. Retorna o valor, que é a substring após "nomeDoCookie="
                return trimmedCookie.substring(prefix.length());
            }
        }

        // Retorna null se o cookie com o nome especificado não for encontrado
        return null;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, null)
                .path("/api")
                .build();
    }
}
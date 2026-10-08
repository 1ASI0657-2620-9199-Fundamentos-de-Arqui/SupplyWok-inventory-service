package aurora.supply_wok.platform.shared.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Validates JWT tokens and extracts claims without local IAM database access.
 */
@Component
@Slf4j
public class JwtTokenValidator {

    @Value("${authorization.jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userIdObject = claims.get("userId");
        if (userIdObject instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}

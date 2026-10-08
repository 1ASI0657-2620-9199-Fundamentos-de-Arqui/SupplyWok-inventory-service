package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.shared.domain.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Provides access to the currently authenticated user in the security context.
 */
@Component
public class CurrentUserProvider {

    /**
     * Retrieves the authenticated user principal.
     *
     * @return the AuthenticatedUser
     * @throws UnauthorizedException if authentication is absent or not an AuthenticatedUser
     */
    public AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }

        if (principal instanceof UserDetails userDetails) {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .toList();
            Long parsedId = null;
            try {
                parsedId = Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException ignored) {
            }
            return new AuthenticatedUser(parsedId != null ? parsedId : 1L, userDetails.getUsername(), roles);
        }

        if (principal instanceof String str) {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .toList();
            Long parsedId = null;
            try {
                parsedId = Long.parseLong(str);
            } catch (NumberFormatException ignored) {
            }
            return new AuthenticatedUser(parsedId != null ? parsedId : 1L, str, roles);
        }

        throw new UnauthorizedException("Authentication principal is not an AuthenticatedUser");
    }

    /**
     * Retrieves the current user's ID (used as restaurant tenant ID).
     *
     * @return the user ID
     */
    public Long userId() {
        Long userId = get().userId();
        if (userId == null) {
            throw new UnauthorizedException("Authenticated user does not have a user ID");
        }
        return userId;
    }

    /**
     * Retrieves the current user's email.
     *
     * @return the user email
     */
    public String email() {
        String email = get().email();
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Authenticated user does not have an email");
        }
        return email;
    }
}

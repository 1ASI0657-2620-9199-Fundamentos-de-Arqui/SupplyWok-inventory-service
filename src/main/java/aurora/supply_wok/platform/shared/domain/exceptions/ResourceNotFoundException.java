package aurora.supply_wok.platform.shared.domain.exceptions;

/**
 * Thrown when a requested resource does not exist or does not belong to the current tenant.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

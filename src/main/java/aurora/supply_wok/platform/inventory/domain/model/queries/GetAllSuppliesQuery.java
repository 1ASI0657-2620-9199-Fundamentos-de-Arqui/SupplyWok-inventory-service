package aurora.supply_wok.platform.inventory.domain.model.queries;

/**
 * Query to retrieve all supplies for a restaurant.
 */
public record GetAllSuppliesQuery(Long restaurantId) {
    public GetAllSuppliesQuery() {
        this(null);
    }
}

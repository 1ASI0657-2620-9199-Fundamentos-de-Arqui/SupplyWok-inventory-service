package aurora.supply_wok.platform.inventory.domain.model.queries;

/**
 * Query to retrieve a supply by id and optionally restaurantId.
 */
public record GetSupplyByIdQuery(Long supplyId, Long restaurantId) {
    public GetSupplyByIdQuery(Long supplyId) {
        this(supplyId, null);
    }
}

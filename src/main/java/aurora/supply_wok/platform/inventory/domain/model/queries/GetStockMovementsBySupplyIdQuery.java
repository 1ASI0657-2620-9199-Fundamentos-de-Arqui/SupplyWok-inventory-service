package aurora.supply_wok.platform.inventory.domain.model.queries;

/**
 * Query to retrieve stock movements by supply id.
 */
public record GetStockMovementsBySupplyIdQuery(Long supplyId) {
}

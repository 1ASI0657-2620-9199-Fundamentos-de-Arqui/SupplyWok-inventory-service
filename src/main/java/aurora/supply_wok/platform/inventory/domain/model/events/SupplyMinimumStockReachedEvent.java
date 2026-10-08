package aurora.supply_wok.platform.inventory.domain.model.events;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;

/**
 * Domain event raised when a supply item reaches or falls below its minimum stock level.
 */
public record SupplyMinimumStockReachedEvent(
        Long supplyId,
        String name,
        int currentStock,
        int minimumStockLevel
) {
    public static SupplyMinimumStockReachedEvent from(Supply supply) {
        return new SupplyMinimumStockReachedEvent(
                supply.getId(),
                supply.getName(),
                supply.getCurrentStock(),
                supply.getMinimumStockLevel()
        );
    }
}

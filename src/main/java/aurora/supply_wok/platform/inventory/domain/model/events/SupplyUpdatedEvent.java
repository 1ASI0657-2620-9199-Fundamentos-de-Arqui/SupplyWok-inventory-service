package aurora.supply_wok.platform.inventory.domain.model.events;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;

/**
 * Domain event raised when supply item data is updated.
 */
public record SupplyUpdatedEvent(
        Long supplyId,
        String name,
        int currentStock,
        int minimumStockLevel,
        String category
) {
    public static SupplyUpdatedEvent from(Supply supply) {
        return new SupplyUpdatedEvent(
                supply.getId(),
                supply.getName(),
                supply.getCurrentStock(),
                supply.getMinimumStockLevel(),
                supply.getCategory()
        );
    }
}

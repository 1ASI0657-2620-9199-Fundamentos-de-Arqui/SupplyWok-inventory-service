package aurora.supply_wok.platform.inventory.domain.model.events;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;

/**
 * Domain event raised when a supply item is created.
 */
public record SupplyCreatedEvent(
        Long supplyId,
        String name,
        int currentStock,
        int minimumStockLevel,
        String category
) {
    public static SupplyCreatedEvent from(Supply supply) {
        return new SupplyCreatedEvent(
                supply.getId(),
                supply.getName(),
                supply.getCurrentStock(),
                supply.getMinimumStockLevel(),
                supply.getCategory()
        );
    }
}

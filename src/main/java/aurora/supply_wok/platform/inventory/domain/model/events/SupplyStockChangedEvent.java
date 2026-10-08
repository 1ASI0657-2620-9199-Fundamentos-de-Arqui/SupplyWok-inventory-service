package aurora.supply_wok.platform.inventory.domain.model.events;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;

/**
 * Domain event raised when stock changes after an inventory movement.
 */
public record SupplyStockChangedEvent(
        Long supplyId,
        String name,
        EMovementType movementType,
        int amount,
        int currentStock,
        int minimumStockLevel
) {
    public static SupplyStockChangedEvent from(Supply supply, EMovementType movementType, int amount) {
        return new SupplyStockChangedEvent(
                supply.getId(),
                supply.getName(),
                movementType,
                amount,
                supply.getCurrentStock(),
                supply.getMinimumStockLevel()
        );
    }
}

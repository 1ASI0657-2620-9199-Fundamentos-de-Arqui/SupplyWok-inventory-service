package aurora.supply_wok.platform.inventory.domain.model.commands;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;

/**
 * Command to create a new supply item.
 */
public record CreateSupplyCommand(
        String name,
        EUnitOfMeasure unitOfMeasure,
        int currentStock,
        int minimumStockLevel,
        String category
) {
}

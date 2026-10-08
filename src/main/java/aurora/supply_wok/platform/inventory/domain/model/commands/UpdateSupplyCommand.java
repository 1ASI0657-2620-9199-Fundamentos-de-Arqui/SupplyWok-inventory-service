package aurora.supply_wok.platform.inventory.domain.model.commands;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;

/**
 * Command to update an existing supply item.
 */
public record UpdateSupplyCommand(
        Long id,
        String name,
        EUnitOfMeasure unitOfMeasure,
        Integer currentStock,
        int minimumStockLevel,
        String category
) {
}

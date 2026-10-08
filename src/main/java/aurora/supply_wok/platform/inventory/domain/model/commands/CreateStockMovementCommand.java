package aurora.supply_wok.platform.inventory.domain.model.commands;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;

import java.time.LocalDateTime;

/**
 * Command to create a stock movement.
 */
public record CreateStockMovementCommand(
        Long supplyId,
        EMovementType type,
        int amount,
        LocalDateTime date,
        String reason
) {
}

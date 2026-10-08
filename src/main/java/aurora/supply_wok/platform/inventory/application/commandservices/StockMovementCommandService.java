package aurora.supply_wok.platform.inventory.application.commandservices;

import aurora.supply_wok.platform.inventory.domain.model.commands.CreateStockMovementCommand;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;

import java.util.Optional;

/**
 * Application contract for stock movement write use cases.
 */
public interface StockMovementCommandService {

    Optional<StockMovement> handle(CreateStockMovementCommand command);
}

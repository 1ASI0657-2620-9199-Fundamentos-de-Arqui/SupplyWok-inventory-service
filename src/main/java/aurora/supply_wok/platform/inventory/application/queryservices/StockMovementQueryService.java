package aurora.supply_wok.platform.inventory.application.queryservices;

import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllStockMovementsQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementsBySupplyIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application contract for stock movement read use cases.
 */
public interface StockMovementQueryService {

    List<StockMovement> handle(GetAllStockMovementsQuery query);

    Optional<StockMovement> handle(GetStockMovementByIdQuery query);

    List<StockMovement> handle(GetStockMovementsBySupplyIdQuery query);
}

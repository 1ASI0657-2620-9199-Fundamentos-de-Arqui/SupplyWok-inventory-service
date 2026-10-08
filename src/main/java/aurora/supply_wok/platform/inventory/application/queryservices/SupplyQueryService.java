package aurora.supply_wok.platform.inventory.application.queryservices;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllSuppliesQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetSupplyByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetTotalSupplyStockQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application contract for supply read use cases.
 */
public interface SupplyQueryService {

    List<Supply> handle(GetAllSuppliesQuery query);

    Optional<Supply> handle(GetSupplyByIdQuery query);

    long handle(GetTotalSupplyStockQuery query);
}

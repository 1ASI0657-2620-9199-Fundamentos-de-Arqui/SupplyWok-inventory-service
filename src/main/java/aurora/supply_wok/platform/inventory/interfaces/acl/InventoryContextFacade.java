package aurora.supply_wok.platform.inventory.interfaces.acl;

import aurora.supply_wok.platform.inventory.application.queryservices.SupplyQueryService;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetTotalSupplyStockQuery;
import org.springframework.stereotype.Service;

/**
 * ACL facade that exposes inventory stock totals to other bounded contexts.
 */
@Service
public class InventoryContextFacade {

    private final SupplyQueryService supplyQueryService;

    public InventoryContextFacade(SupplyQueryService supplyQueryService) {
        this.supplyQueryService = supplyQueryService;
    }

    public long getTotalSupplyStock() {
        return supplyQueryService.handle(new GetTotalSupplyStockQuery());
    }
}

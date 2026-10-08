package aurora.supply_wok.platform.inventory.interfaces.rest.transform;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.SupplyResource;

/**
 * Maps supply domain entities to response resources.
 */
public final class SupplyResourceFromEntityAssembler {

    private SupplyResourceFromEntityAssembler() {
    }

    public static SupplyResource toResourceFromEntity(Supply supply) {
        return new SupplyResource(
                supply.getId(),
                supply.getName(),
                supply.getUnitOfMeasure().name(),
                supply.getCurrentStock(),
                supply.getMinimumStockLevel(),
                supply.getCategory()
        );
    }
}

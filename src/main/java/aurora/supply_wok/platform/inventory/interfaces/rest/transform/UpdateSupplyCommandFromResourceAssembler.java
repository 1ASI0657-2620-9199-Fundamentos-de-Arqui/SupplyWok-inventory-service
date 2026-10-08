package aurora.supply_wok.platform.inventory.interfaces.rest.transform;

import aurora.supply_wok.platform.inventory.domain.model.commands.UpdateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.UpdateSupplyResource;

/**
 * Maps update supply requests to commands.
 */
public final class UpdateSupplyCommandFromResourceAssembler {

    private UpdateSupplyCommandFromResourceAssembler() {
    }

    public static UpdateSupplyCommand toCommandFromResource(Long supplyId, UpdateSupplyResource resource) {
        return new UpdateSupplyCommand(
                supplyId,
                resource.name(),
                EUnitOfMeasure.valueOf(resource.unitOfMeasure().trim()),
                resource.currentStock(),
                resource.minimumStockLevel(),
                resource.category()
        );
    }
}

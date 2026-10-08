package aurora.supply_wok.platform.inventory.interfaces.rest.transform;

import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.CreateSupplyResource;

/**
 * Maps create supply requests to commands.
 */
public final class CreateSupplyCommandFromResourceAssembler {

    private CreateSupplyCommandFromResourceAssembler() {
    }

    public static CreateSupplyCommand toCommandFromResource(CreateSupplyResource resource) {
        return new CreateSupplyCommand(
                resource.name(),
                EUnitOfMeasure.valueOf(resource.unitOfMeasure().trim()),
                resource.currentStock(),
                resource.minimumStockLevel(),
                resource.category()
        );
    }
}

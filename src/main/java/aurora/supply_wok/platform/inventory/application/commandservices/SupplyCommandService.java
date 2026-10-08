package aurora.supply_wok.platform.inventory.application.commandservices;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.DeleteSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.UpdateSupplyCommand;

import java.util.Optional;

/**
 * Application contract for supply write use cases.
 */
public interface SupplyCommandService {

    Supply handle(CreateSupplyCommand command);

    Optional<Supply> handle(UpdateSupplyCommand command);

    boolean handle(DeleteSupplyCommand command);
}

package aurora.supply_wok.platform.inventory.application.internal.commandservices;

import aurora.supply_wok.platform.inventory.application.commandservices.SupplyCommandService;
import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.DeleteSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.UpdateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default supply command service implementation.
 */
@Service
public class SupplyCommandServiceImpl implements SupplyCommandService {

    private final SupplyRepository supplyRepository;

    public SupplyCommandServiceImpl(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override
    public Supply handle(CreateSupplyCommand command) {
        return supplyRepository.save(new Supply(command));
    }

    @Override
    public Optional<Supply> handle(UpdateSupplyCommand command) {
        var supply = supplyRepository.findById(command.id());
        if (supply.isEmpty()) {
            return Optional.empty();
        }

        supply.get().update(
                command.name(),
                command.unitOfMeasure(),
                command.currentStock(),
                command.minimumStockLevel(),
                command.category()
        );
        return Optional.of(supplyRepository.save(supply.get()));
    }

    @Override
    public boolean handle(DeleteSupplyCommand command) {
        var supply = supplyRepository.findById(command.id());
        if (supply.isEmpty()) {
            return false;
        }

        supplyRepository.delete(supply.get());
        return true;
    }
}

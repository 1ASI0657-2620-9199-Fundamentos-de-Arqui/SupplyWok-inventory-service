package aurora.supply_wok.platform.inventory.application.internal.commandservices;

import aurora.supply_wok.platform.inventory.application.commandservices.StockMovementCommandService;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateStockMovementCommand;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default stock movement command service implementation.
 */
@Service
public class StockMovementCommandServiceImpl implements StockMovementCommandService {

    private final SupplyRepository supplyRepository;

    public StockMovementCommandServiceImpl(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override
    public Optional<StockMovement> handle(CreateStockMovementCommand command) {
        var supply = supplyRepository.findById(command.supplyId());
        if (supply.isEmpty()) {
            return Optional.empty();
        }

        supply.get().applyMovement(command.type(), command.amount());
        supplyRepository.save(supply.get());

        var movement = new StockMovement(
                command.supplyId(),
                command.type(),
                command.amount(),
                command.date(),
                command.reason()
        );
        return Optional.of(supplyRepository.saveStockMovement(movement));
    }
}

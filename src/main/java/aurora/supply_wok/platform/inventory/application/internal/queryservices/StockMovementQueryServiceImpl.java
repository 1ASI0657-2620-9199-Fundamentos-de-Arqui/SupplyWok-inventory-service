package aurora.supply_wok.platform.inventory.application.internal.queryservices;

import aurora.supply_wok.platform.inventory.application.queryservices.StockMovementQueryService;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllStockMovementsQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementsBySupplyIdQuery;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default stock movement query service implementation.
 */
@Service
public class StockMovementQueryServiceImpl implements StockMovementQueryService {

    private final SupplyRepository supplyRepository;

    public StockMovementQueryServiceImpl(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override
    public List<StockMovement> handle(GetAllStockMovementsQuery query) {
        return supplyRepository.findAllStockMovements();
    }

    @Override
    public Optional<StockMovement> handle(GetStockMovementByIdQuery query) {
        return supplyRepository.findStockMovementById(query.stockMovementId());
    }

    @Override
    public List<StockMovement> handle(GetStockMovementsBySupplyIdQuery query) {
        return supplyRepository.findStockMovementsBySupplyId(query.supplyId());
    }
}

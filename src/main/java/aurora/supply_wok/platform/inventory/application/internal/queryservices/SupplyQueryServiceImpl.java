package aurora.supply_wok.platform.inventory.application.internal.queryservices;

import aurora.supply_wok.platform.inventory.application.queryservices.SupplyQueryService;
import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllSuppliesQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetSupplyByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetTotalSupplyStockQuery;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default supply query service implementation.
 */
@Service
public class SupplyQueryServiceImpl implements SupplyQueryService {

    private final SupplyRepository supplyRepository;

    public SupplyQueryServiceImpl(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override
    public List<Supply> handle(GetAllSuppliesQuery query) {
        if (query.restaurantId() != null) {
            return supplyRepository.findAllByRestaurantId(query.restaurantId());
        }
        return supplyRepository.findAll();
    }

    @Override
    public Optional<Supply> handle(GetSupplyByIdQuery query) {
        if (query.restaurantId() != null) {
            return supplyRepository.findByIdAndRestaurantId(query.supplyId(), query.restaurantId());
        }
        return supplyRepository.findById(query.supplyId());
    }

    @Override
    public long handle(GetTotalSupplyStockQuery query) {
        if (query.restaurantId() != null) {
            return supplyRepository.getTotalCurrentStockByRestaurantId(query.restaurantId());
        }
        return supplyRepository.getTotalCurrentStock();
    }
}

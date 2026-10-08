package aurora.supply_wok.platform.inventory.domain.repositories;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;

import java.util.List;
import java.util.Optional;

/**
 * Inventory repository port.
 */
public interface SupplyRepository {

    List<Supply> findAll();

    Optional<Supply> findById(Long id);

    Supply save(Supply supply);

    void delete(Supply supply);

    long getTotalCurrentStock();

    StockMovement saveStockMovement(StockMovement stockMovement);

    List<StockMovement> findAllStockMovements();

    Optional<StockMovement> findStockMovementById(Long id);

    List<StockMovement> findStockMovementsBySupplyId(Long supplyId);
}

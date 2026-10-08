package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.StockMovementPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for stock movement persistence entities.
 */
@Repository
public interface StockMovementPersistenceRepository extends JpaRepository<StockMovementPersistenceEntity, Long> {

    List<StockMovementPersistenceEntity> findAllByOrderByDateDesc();

    List<StockMovementPersistenceEntity> findAllBySupplyIdOrderByDateDesc(Long supplyId);
}

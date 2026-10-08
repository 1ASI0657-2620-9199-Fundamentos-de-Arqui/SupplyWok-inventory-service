package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.SupplyPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for supply persistence entities.
 */
@Repository
public interface SupplyPersistenceRepository extends JpaRepository<SupplyPersistenceEntity, Long> {

    List<SupplyPersistenceEntity> findAllByOrderByIdAsc();

    List<SupplyPersistenceEntity> findAllByRestaurantIdOrderByIdAsc(Long restaurantId);

    Optional<SupplyPersistenceEntity> findByIdAndRestaurantId(Long id, Long restaurantId);

    @Query("select coalesce(sum(s.currentStock), 0) from SupplyPersistenceEntity s")
    Long sumCurrentStock();

    @Query("select coalesce(sum(s.currentStock), 0) from SupplyPersistenceEntity s where s.restaurantId = :restaurantId")
    Long sumCurrentStockByRestaurantId(@Param("restaurantId") Long restaurantId);
}

package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.SupplyPersistenceEntity;

/**
 * Static mapper between supply domain and persistence representations.
 */
public final class SupplyPersistenceAssembler {

    private SupplyPersistenceAssembler() {
    }

    public static Supply toDomainFromPersistence(SupplyPersistenceEntity entity) {
        var supply = new Supply(
                entity.getRestaurantId(),
                entity.getName(),
                entity.getUnitOfMeasure(),
                entity.getCurrentStock(),
                entity.getMinimumStockLevel(),
                entity.getCategory()
        );
        supply.setId(entity.getId());
        return supply;
    }

    public static SupplyPersistenceEntity toPersistenceFromDomain(Supply supply) {
        var entity = new SupplyPersistenceEntity();
        entity.setId(supply.getId());
        entity.setRestaurantId(supply.getRestaurantId() != null ? supply.getRestaurantId() : 1L);
        entity.setName(supply.getName());
        entity.setUnitOfMeasure(supply.getUnitOfMeasure());
        entity.setCurrentStock(supply.getCurrentStock());
        entity.setMinimumStockLevel(supply.getMinimumStockLevel());
        entity.setCategory(supply.getCategory());
        return entity;
    }
}

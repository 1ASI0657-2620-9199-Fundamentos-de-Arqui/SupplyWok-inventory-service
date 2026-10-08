package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.StockMovementPersistenceEntity;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.SupplyPersistenceEntity;

/**
 * Static mapper between stock movement domain and persistence representations.
 */
public final class StockMovementPersistenceAssembler {

    private StockMovementPersistenceAssembler() {
    }

    public static StockMovement toDomainFromPersistence(StockMovementPersistenceEntity entity) {
        var supplyId = entity.getSupplyId() != null
                ? entity.getSupplyId()
                : (entity.getSupply() != null ? entity.getSupply().getId() : null);
        var movement = new StockMovement(
                supplyId,
                entity.getType(),
                entity.getAmount(),
                entity.getDate(),
                entity.getReason()
        );
        movement.setId(entity.getId());
        return movement;
    }

    public static StockMovementPersistenceEntity toPersistenceFromDomain(StockMovement movement, SupplyPersistenceEntity supply) {
        var entity = new StockMovementPersistenceEntity();
        entity.setId(movement.getId());
        entity.setSupply(supply);
        if (supply != null) {
            entity.setSupplyId(supply.getId());
        }
        entity.setType(movement.getType());
        entity.setAmount(movement.getAmount());
        entity.setDate(movement.getDate());
        entity.setReason(movement.getReason());
        return entity;
    }
}

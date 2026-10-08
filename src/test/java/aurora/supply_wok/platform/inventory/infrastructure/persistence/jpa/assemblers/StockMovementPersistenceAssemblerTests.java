package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.StockMovementPersistenceEntity;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.SupplyPersistenceEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StockMovementPersistenceAssemblerTests {

    @Test
    void toDomainFromPersistence_withExplicitSupplyId_mapsAllFieldsCorrectly() {
        // Arrange
        var entity = new StockMovementPersistenceEntity();
        entity.setId(100L);
        entity.setSupplyId(5L);
        entity.setType(EMovementType.Entry);
        entity.setAmount(30);
        entity.setDate(LocalDateTime.of(2026, 3, 10, 14, 0));
        entity.setReason("Delivery #104");

        // Act
        var domain = StockMovementPersistenceAssembler.toDomainFromPersistence(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(100L);
        assertThat(domain.getSupplyId()).isEqualTo(5L);
        assertThat(domain.getType()).isEqualTo(EMovementType.Entry);
        assertThat(domain.getAmount()).isEqualTo(30);
        assertThat(domain.getDate()).isEqualTo(LocalDateTime.of(2026, 3, 10, 14, 0));
        assertThat(domain.getReason()).isEqualTo("Delivery #104");
    }

    @Test
    void toDomainFromPersistence_whenSupplyIdIsNullAndSupplyEntityPresent_fallsBackToSupplyEntityId() {
        // Arrange
        var supplyEntity = new SupplyPersistenceEntity();
        supplyEntity.setId(77L);

        var entity = new StockMovementPersistenceEntity();
        entity.setId(200L);
        entity.setSupplyId(null);
        entity.setSupply(supplyEntity);
        entity.setType(EMovementType.Exit);
        entity.setAmount(5);
        entity.setDate(LocalDateTime.of(2026, 3, 11, 9, 30));
        entity.setReason("Wok station prep");

        // Act
        var domain = StockMovementPersistenceAssembler.toDomainFromPersistence(entity);

        // Assert
        assertThat(domain.getSupplyId()).isEqualTo(77L);
        assertThat(domain.getType()).isEqualTo(EMovementType.Exit);
        assertThat(domain.getAmount()).isEqualTo(5);
    }

    @Test
    void toPersistenceFromDomain_withValidMovementAndSupplyEntity_mapsAllFieldsCorrectly() {
        // Arrange
        var domain = new StockMovement(12L, EMovementType.Exit, 8, LocalDateTime.of(2026, 3, 12, 11, 0), "Daily consumption");
        domain.setId(300L);

        var supplyEntity = new SupplyPersistenceEntity();
        supplyEntity.setId(12L);

        // Act
        var entity = StockMovementPersistenceAssembler.toPersistenceFromDomain(domain, supplyEntity);

        // Assert
        assertThat(entity.getId()).isEqualTo(300L);
        assertThat(entity.getSupply()).isEqualTo(supplyEntity);
        assertThat(entity.getSupplyId()).isEqualTo(12L);
        assertThat(entity.getType()).isEqualTo(EMovementType.Exit);
        assertThat(entity.getAmount()).isEqualTo(8);
        assertThat(entity.getDate()).isEqualTo(LocalDateTime.of(2026, 3, 12, 11, 0));
        assertThat(entity.getReason()).isEqualTo("Daily consumption");
    }
}

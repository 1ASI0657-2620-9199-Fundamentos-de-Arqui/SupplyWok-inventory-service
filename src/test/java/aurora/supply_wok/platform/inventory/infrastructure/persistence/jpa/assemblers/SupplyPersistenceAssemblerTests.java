package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.SupplyPersistenceEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupplyPersistenceAssemblerTests {

    @Test
    void toDomainFromPersistence_withValidEntity_mapsAllFieldsCorrectly() {
        // Arrange
        var entity = new SupplyPersistenceEntity();
        entity.setId(10L);
        entity.setRestaurantId(5L);
        entity.setName("Rice Vinegar");
        entity.setUnitOfMeasure(EUnitOfMeasure.Liters);
        entity.setCurrentStock(40);
        entity.setMinimumStockLevel(10);
        entity.setCategory("Condiments");

        // Act
        var supply = SupplyPersistenceAssembler.toDomainFromPersistence(entity);

        // Assert
        assertThat(supply.getId()).isEqualTo(10L);
        assertThat(supply.getRestaurantId()).isEqualTo(5L);
        assertThat(supply.getName()).isEqualTo("Rice Vinegar");
        assertThat(supply.getUnitOfMeasure()).isEqualTo(EUnitOfMeasure.Liters);
        assertThat(supply.getCurrentStock()).isEqualTo(40);
        assertThat(supply.getMinimumStockLevel()).isEqualTo(10);
        assertThat(supply.getCategory()).isEqualTo("Condiments");
    }

    @Test
    void toPersistenceFromDomain_withValidDomainModel_mapsAllFieldsCorrectly() {
        // Arrange
        var supply = new Supply(5L, "Dark Soy Sauce", EUnitOfMeasure.Liters, 25, 5, "Sauces");
        supply.setId(22L);

        // Act
        var entity = SupplyPersistenceAssembler.toPersistenceFromDomain(supply);

        // Assert
        assertThat(entity.getId()).isEqualTo(22L);
        assertThat(entity.getRestaurantId()).isEqualTo(5L);
        assertThat(entity.getName()).isEqualTo("Dark Soy Sauce");
        assertThat(entity.getUnitOfMeasure()).isEqualTo(EUnitOfMeasure.Liters);
        assertThat(entity.getCurrentStock()).isEqualTo(25);
        assertThat(entity.getMinimumStockLevel()).isEqualTo(5);
        assertThat(entity.getCategory()).isEqualTo("Sauces");
    }
}

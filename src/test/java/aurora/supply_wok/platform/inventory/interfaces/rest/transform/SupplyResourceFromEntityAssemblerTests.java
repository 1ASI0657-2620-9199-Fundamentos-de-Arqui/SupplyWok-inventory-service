package aurora.supply_wok.platform.inventory.interfaces.rest.transform;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupplyResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_withValidSupply_mapsAllFieldsCorrectly() {
        // Arrange
        var supply = new Supply(
                "White Pepper",
                EUnitOfMeasure.Grams,
                300,
                50,
                "Spices"
        );
        supply.setId(88L);

        // Act
        var resource = SupplyResourceFromEntityAssembler.toResourceFromEntity(supply);

        // Assert
        assertThat(resource.id()).isEqualTo(88L);
        assertThat(resource.name()).isEqualTo("White Pepper");
        assertThat(resource.unitOfMeasure()).isEqualTo("Grams");
        assertThat(resource.currentStock()).isEqualTo(300);
        assertThat(resource.minimumStockLevel()).isEqualTo(50);
        assertThat(resource.category()).isEqualTo("Spices");
    }
}

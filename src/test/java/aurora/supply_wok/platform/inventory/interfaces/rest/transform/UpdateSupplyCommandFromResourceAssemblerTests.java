package aurora.supply_wok.platform.inventory.interfaces.rest.transform;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.UpdateSupplyResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateSupplyCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_withValidResourceAndId_mapsAllFieldsCorrectly() {
        // Arrange
        Long supplyId = 42L;
        var resource = new UpdateSupplyResource(
                "Fresh Basil",
                "Units",
                25,
                5,
                "Herbs"
        );

        // Act
        var command = UpdateSupplyCommandFromResourceAssembler.toCommandFromResource(supplyId, resource);

        // Assert
        assertThat(command.id()).isEqualTo(42L);
        assertThat(command.name()).isEqualTo("Fresh Basil");
        assertThat(command.unitOfMeasure()).isEqualTo(EUnitOfMeasure.Units);
        assertThat(command.currentStock()).isEqualTo(25);
        assertThat(command.minimumStockLevel()).isEqualTo(5);
        assertThat(command.category()).isEqualTo("Herbs");
    }

    @Test
    void toCommandFromResource_withWhitespaceInUnitOfMeasure_trimsAndParsesEnum() {
        // Arrange
        Long supplyId = 15L;
        var resource = new UpdateSupplyResource(
                "Salt",
                " Grams ",
                500,
                50,
                "Spices"
        );

        // Act
        var command = UpdateSupplyCommandFromResourceAssembler.toCommandFromResource(supplyId, resource);

        // Assert
        assertThat(command.unitOfMeasure()).isEqualTo(EUnitOfMeasure.Grams);
    }
}

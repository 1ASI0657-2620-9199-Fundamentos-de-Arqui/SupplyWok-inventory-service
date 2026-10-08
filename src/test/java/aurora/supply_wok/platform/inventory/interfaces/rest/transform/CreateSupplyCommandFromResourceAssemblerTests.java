package aurora.supply_wok.platform.inventory.interfaces.rest.transform;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.CreateSupplyResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSupplyCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_withValidResource_mapsAllFieldsCorrectly() {
        // Arrange
        var resource = new CreateSupplyResource(
                "Fresh Ginger",
                "Kilograms",
                15,
                3,
                "Vegetables"
        );

        // Act
        var command = CreateSupplyCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.name()).isEqualTo("Fresh Ginger");
        assertThat(command.unitOfMeasure()).isEqualTo(EUnitOfMeasure.Kilograms);
        assertThat(command.currentStock()).isEqualTo(15);
        assertThat(command.minimumStockLevel()).isEqualTo(3);
        assertThat(command.category()).isEqualTo("Vegetables");
    }

    @Test
    void toCommandFromResource_withWhitespaceInUnitOfMeasure_trimsAndParsesEnum() {
        // Arrange
        var resource = new CreateSupplyResource(
                "Sesame Oil",
                " Liters ",
                5,
                1,
                "Oils"
        );

        // Act
        var command = CreateSupplyCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.unitOfMeasure()).isEqualTo(EUnitOfMeasure.Liters);
    }
}

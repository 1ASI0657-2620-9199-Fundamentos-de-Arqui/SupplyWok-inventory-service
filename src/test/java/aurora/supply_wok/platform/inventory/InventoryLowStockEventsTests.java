package aurora.supply_wok.platform.inventory;

import aurora.supply_wok.platform.inventory.application.commandservices.SupplyCommandService;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.UpdateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
class InventoryLowStockEventsTests {

    @Autowired
    private SupplyCommandService supplyCommandService;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void handle_whenCreatedSupplyStartsAtMinimumStock_publishesIntegrationEvent() {
        // Arrange
        var command = new CreateSupplyCommand(
                "Test low stock rice",
                EUnitOfMeasure.Kilograms,
                2,
                5,
                "Grains"
        );

        // Act
        var supply = supplyCommandService.handle(command);

        // Assert
        assertThat(supply).isNotNull();
        assertThat(supply.getCurrentStock()).isEqualTo(2);

        var events = applicationEvents.stream(SupplyMinimumStockReachedIntegrationEvent.class).toList();
        assertThat(events)
                .anySatisfy(event -> {
                    assertThat(event.supplyId()).isEqualTo(supply.getId());
                    assertThat(event.name()).isEqualTo("Test low stock rice");
                    assertThat(event.currentStock()).isEqualTo(2);
                    assertThat(event.minimumStockLevel()).isEqualTo(5);
                });
    }

    @Test
    void handle_whenSupplyUpdateReachesMinimumStock_publishesIntegrationEvent() {
        // Arrange
        var supply = supplyCommandService.handle(new CreateSupplyCommand(
                "Test updated low stock noodles",
                EUnitOfMeasure.Units,
                30,
                5,
                "Dry goods"
        ));

        var updateCommand = new UpdateSupplyCommand(
                supply.getId(),
                supply.getName(),
                supply.getUnitOfMeasure(),
                3,
                5,
                supply.getCategory()
        );

        // Act
        var updatedSupply = supplyCommandService.handle(updateCommand);

        // Assert
        assertThat(updatedSupply).isPresent();
        assertThat(updatedSupply.get().getCurrentStock()).isEqualTo(3);

        var events = applicationEvents.stream(SupplyMinimumStockReachedIntegrationEvent.class).toList();
        assertThat(events)
                .anySatisfy(event -> {
                    assertThat(event.supplyId()).isEqualTo(supply.getId());
                    assertThat(event.name()).isEqualTo("Test updated low stock noodles");
                    assertThat(event.currentStock()).isEqualTo(3);
                    assertThat(event.minimumStockLevel()).isEqualTo(5);
                });
    }
}

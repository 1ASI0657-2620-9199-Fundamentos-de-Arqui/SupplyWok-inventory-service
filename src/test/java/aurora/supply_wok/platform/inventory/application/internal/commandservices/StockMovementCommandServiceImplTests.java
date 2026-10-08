package aurora.supply_wok.platform.inventory.application.internal.commandservices;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateStockMovementCommand;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockMovementCommandServiceImplTests {

    @Mock
    private SupplyRepository supplyRepository;

    @InjectMocks
    private StockMovementCommandServiceImpl stockMovementCommandService;

    @Test
    void handleCreateStockMovementCommand_whenSupplyExistsAndEntryMovement_appliesMovementSavesBothAndReturnsMovement() {
        // Arrange
        var supply = new Supply("Garlic", EUnitOfMeasure.Kilograms, 10, 2, "Produce");
        supply.setId(1L);
        when(supplyRepository.findById(1L)).thenReturn(Optional.of(supply));

        var movement = new StockMovement(1L, EMovementType.Entry, 5, LocalDateTime.now(), "Restock shipment");
        movement.setId(100L);
        when(supplyRepository.saveStockMovement(any(StockMovement.class))).thenReturn(movement);

        var command = new CreateStockMovementCommand(1L, EMovementType.Entry, 5, LocalDateTime.now(), "Restock shipment");

        // Act
        var result = stockMovementCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(100L);
        assertThat(supply.getCurrentStock()).isEqualTo(15);
        verify(supplyRepository).save(supply);
        verify(supplyRepository).saveStockMovement(any(StockMovement.class));
    }

    @Test
    void handleCreateStockMovementCommand_whenSupplyNotFound_returnsEmptyAndNeverSaves() {
        // Arrange
        when(supplyRepository.findById(999L)).thenReturn(Optional.empty());
        var command = new CreateStockMovementCommand(999L, EMovementType.Entry, 10, LocalDateTime.now(), "Restock");

        // Act
        var result = stockMovementCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(supplyRepository, never()).save(any(Supply.class));
        verify(supplyRepository, never()).saveStockMovement(any(StockMovement.class));
    }

    @Test
    void handleCreateStockMovementCommand_whenExitMovementExceedsStock_throwsExceptionAndNeverSavesMovement() {
        // Arrange
        var supply = new Supply("Garlic", EUnitOfMeasure.Kilograms, 5, 2, "Produce");
        supply.setId(1L);
        when(supplyRepository.findById(1L)).thenReturn(Optional.of(supply));

        var command = new CreateStockMovementCommand(1L, EMovementType.Exit, 10, LocalDateTime.now(), "Order consumption");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                stockMovementCommandService.handle(command));
        assertThat(exception.getMessage()).isEqualTo("Current stock cannot be negative.");
        verify(supplyRepository, never()).saveStockMovement(any(StockMovement.class));
    }
}

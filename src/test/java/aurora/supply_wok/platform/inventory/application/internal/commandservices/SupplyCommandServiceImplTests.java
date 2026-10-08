package aurora.supply_wok.platform.inventory.application.internal.commandservices;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.DeleteSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.UpdateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplyCommandServiceImplTests {

    @Mock
    private SupplyRepository supplyRepository;

    @InjectMocks
    private SupplyCommandServiceImpl supplyCommandService;

    @Test
    void handleCreateSupplyCommand_withValidCommand_savesAndReturnsSupply() {
        // Arrange
        var command = new CreateSupplyCommand("Noodles", EUnitOfMeasure.Kilograms, 20, 5, "Dry goods");
        var savedSupply = new Supply(command);
        savedSupply.setId(10L);
        when(supplyRepository.save(any(Supply.class))).thenReturn(savedSupply);

        // Act
        var result = supplyCommandService.handle(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Noodles");
        verify(supplyRepository).save(any(Supply.class));
    }

    @Test
    void handleUpdateSupplyCommand_whenSupplyExists_updatesSavesAndReturnsOptional() {
        // Arrange
        var existingSupply = new Supply("Noodles", EUnitOfMeasure.Kilograms, 20, 5, "Dry goods");
        existingSupply.setId(1L);
        when(supplyRepository.findById(1L)).thenReturn(Optional.of(existingSupply));
        when(supplyRepository.save(existingSupply)).thenReturn(existingSupply);

        var updateCommand = new UpdateSupplyCommand(1L, "Egg Noodles", EUnitOfMeasure.Kilograms, 25, 8, "Pasta");

        // Act
        var result = supplyCommandService.handle(updateCommand);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Egg Noodles");
        assertThat(result.get().getCurrentStock()).isEqualTo(25);
        assertThat(result.get().getMinimumStockLevel()).isEqualTo(8);
        assertThat(result.get().getCategory()).isEqualTo("Pasta");
        verify(supplyRepository).save(existingSupply);
    }

    @Test
    void handleUpdateSupplyCommand_whenSupplyDoesNotExist_returnsEmptyAndNeverSaves() {
        // Arrange
        when(supplyRepository.findById(999L)).thenReturn(Optional.empty());
        var updateCommand = new UpdateSupplyCommand(999L, "Egg Noodles", EUnitOfMeasure.Kilograms, 25, 8, "Pasta");

        // Act
        var result = supplyCommandService.handle(updateCommand);

        // Assert
        assertThat(result).isEmpty();
        verify(supplyRepository, never()).save(any(Supply.class));
    }

    @Test
    void handleDeleteSupplyCommand_whenSupplyExists_deletesAndReturnsTrue() {
        // Arrange
        var existingSupply = new Supply("Noodles", EUnitOfMeasure.Kilograms, 20, 5, "Dry goods");
        existingSupply.setId(1L);
        when(supplyRepository.findById(1L)).thenReturn(Optional.of(existingSupply));

        var deleteCommand = new DeleteSupplyCommand(1L);

        // Act
        var result = supplyCommandService.handle(deleteCommand);

        // Assert
        assertThat(result).isTrue();
        verify(supplyRepository).delete(existingSupply);
    }

    @Test
    void handleDeleteSupplyCommand_whenSupplyDoesNotExist_returnsFalseAndNeverDeletes() {
        // Arrange
        when(supplyRepository.findById(999L)).thenReturn(Optional.empty());
        var deleteCommand = new DeleteSupplyCommand(999L);

        // Act
        var result = supplyCommandService.handle(deleteCommand);

        // Assert
        assertThat(result).isFalse();
        verify(supplyRepository, never()).delete(any(Supply.class));
    }
}

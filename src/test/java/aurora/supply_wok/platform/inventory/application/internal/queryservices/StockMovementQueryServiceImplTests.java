package aurora.supply_wok.platform.inventory.application.internal.queryservices;

import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllStockMovementsQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementsBySupplyIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockMovementQueryServiceImplTests {

    @Mock
    private SupplyRepository supplyRepository;

    @InjectMocks
    private StockMovementQueryServiceImpl stockMovementQueryService;

    @Test
    void handleGetAllStockMovementsQuery_whenCalled_returnsMovementsFromRepository() {
        // Arrange
        var movement = new StockMovement(1L, EMovementType.Entry, 10, LocalDateTime.now(), "Restock");
        when(supplyRepository.findAllStockMovements()).thenReturn(List.of(movement));

        // Act
        var result = stockMovementQueryService.handle(new GetAllStockMovementsQuery());

        // Assert
        assertThat(result).containsExactly(movement);
        verify(supplyRepository).findAllStockMovements();
    }

    @Test
    void handleGetStockMovementByIdQuery_whenCalled_delegatesToRepository() {
        // Arrange
        var movement = new StockMovement(2L, EMovementType.Exit, 3, LocalDateTime.now(), "Damaged goods");
        movement.setId(10L);
        when(supplyRepository.findStockMovementById(10L)).thenReturn(Optional.of(movement));

        // Act
        var result = stockMovementQueryService.handle(new GetStockMovementByIdQuery(10L));

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(10L);
        verify(supplyRepository).findStockMovementById(10L);
    }

    @Test
    void handleGetStockMovementsBySupplyIdQuery_whenCalled_delegatesToRepository() {
        // Arrange
        var movement1 = new StockMovement(3L, EMovementType.Entry, 5, LocalDateTime.now(), "Restock");
        var movement2 = new StockMovement(3L, EMovementType.Exit, 2, LocalDateTime.now(), "Kitchen use");
        when(supplyRepository.findStockMovementsBySupplyId(3L)).thenReturn(List.of(movement1, movement2));

        // Act
        var result = stockMovementQueryService.handle(new GetStockMovementsBySupplyIdQuery(3L));

        // Assert
        assertThat(result).containsExactly(movement1, movement2);
        verify(supplyRepository).findStockMovementsBySupplyId(3L);
    }
}

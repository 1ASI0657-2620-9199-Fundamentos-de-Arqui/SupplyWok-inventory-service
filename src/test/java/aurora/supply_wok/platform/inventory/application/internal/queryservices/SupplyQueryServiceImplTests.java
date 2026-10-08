package aurora.supply_wok.platform.inventory.application.internal.queryservices;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllSuppliesQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetSupplyByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetTotalSupplyStockQuery;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplyQueryServiceImplTests {

    @Mock
    private SupplyRepository supplyRepository;

    @InjectMocks
    private SupplyQueryServiceImpl supplyQueryService;

    @Test
    void handleGetAllSuppliesQuery_whenCalled_returnsAllSuppliesFromRepository() {
        // Arrange
        var supply1 = new Supply("Carrot", EUnitOfMeasure.Kilograms, 10, 2, "Vegetables");
        var supply2 = new Supply("Cabbage", EUnitOfMeasure.Kilograms, 15, 3, "Vegetables");
        when(supplyRepository.findAll()).thenReturn(List.of(supply1, supply2));

        // Act
        var result = supplyQueryService.handle(new GetAllSuppliesQuery());

        // Assert
        assertThat(result).containsExactly(supply1, supply2);
        verify(supplyRepository).findAll();
    }

    @Test
    void handleGetSupplyByIdQuery_whenCalled_delegatesToRepository() {
        // Arrange
        var supply = new Supply("Broccoli", EUnitOfMeasure.Kilograms, 8, 2, "Vegetables");
        supply.setId(5L);
        when(supplyRepository.findById(5L)).thenReturn(Optional.of(supply));

        // Act
        var result = supplyQueryService.handle(new GetSupplyByIdQuery(5L));

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Broccoli");
        verify(supplyRepository).findById(5L);
    }

    @Test
    void handleGetTotalSupplyStockQuery_whenCalled_returnsTotalStockFromRepository() {
        // Arrange
        when(supplyRepository.getTotalCurrentStock()).thenReturn(450L);

        // Act
        var result = supplyQueryService.handle(new GetTotalSupplyStockQuery());

        // Assert
        assertThat(result).isEqualTo(450L);
        verify(supplyRepository).getTotalCurrentStock();
    }
}

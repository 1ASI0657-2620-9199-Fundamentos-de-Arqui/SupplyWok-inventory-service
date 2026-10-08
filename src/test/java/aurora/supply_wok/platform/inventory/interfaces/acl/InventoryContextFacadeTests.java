package aurora.supply_wok.platform.inventory.interfaces.acl;

import aurora.supply_wok.platform.inventory.application.queryservices.SupplyQueryService;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetTotalSupplyStockQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryContextFacadeTests {

    @Mock
    private SupplyQueryService supplyQueryService;

    @InjectMocks
    private InventoryContextFacade facade;

    @Test
    void getTotalSupplyStock_whenCalled_delegatesToSupplyQueryService() {
        // Arrange
        when(supplyQueryService.handle(any(GetTotalSupplyStockQuery.class))).thenReturn(1200L);

        // Act
        long totalStock = facade.getTotalSupplyStock();

        // Assert
        assertThat(totalStock).isEqualTo(1200L);
        verify(supplyQueryService).handle(any(GetTotalSupplyStockQuery.class));
    }
}

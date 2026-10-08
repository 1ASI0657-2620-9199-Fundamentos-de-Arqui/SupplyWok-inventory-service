package aurora.supply_wok.platform.inventory.application.internal.eventhandlers;

import aurora.supply_wok.platform.inventory.domain.model.events.SupplyMinimumStockReachedEvent;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SupplyMinimumStockReachedEventHandlerTests {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SupplyMinimumStockReachedEventHandler handler;

    @Test
    void on_whenDomainEventReceived_publishesIntegrationEvent() {
        // Arrange
        var domainEvent = new SupplyMinimumStockReachedEvent(7L, "Soy Sauce", 2, 5);
        var captor = ArgumentCaptor.forClass(SupplyMinimumStockReachedIntegrationEvent.class);

        // Act
        handler.on(domainEvent);

        // Assert
        verify(eventPublisher).publishEvent(captor.capture());
        var integrationEvent = captor.getValue();
        assertThat(integrationEvent.supplyId()).isEqualTo(7L);
        assertThat(integrationEvent.name()).isEqualTo("Soy Sauce");
        assertThat(integrationEvent.currentStock()).isEqualTo(2);
        assertThat(integrationEvent.minimumStockLevel()).isEqualTo(5);
    }
}

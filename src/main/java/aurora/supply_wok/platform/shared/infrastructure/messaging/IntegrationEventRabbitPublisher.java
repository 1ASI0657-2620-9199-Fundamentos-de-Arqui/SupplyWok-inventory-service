package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

/**
 * Publishes inventory integration events to RabbitMQ.
 */
@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationEventRabbitPublisher {

    private final EventOutbox outbox;

    public IntegrationEventRabbitPublisher(EventOutbox outbox) {
        this.outbox = outbox;
    }

    @EventListener
    public void handle(SupplyMinimumStockReachedIntegrationEvent event) {
        outbox.enqueue("inventory.supply.minimum-stock-reached", event);
    }
}

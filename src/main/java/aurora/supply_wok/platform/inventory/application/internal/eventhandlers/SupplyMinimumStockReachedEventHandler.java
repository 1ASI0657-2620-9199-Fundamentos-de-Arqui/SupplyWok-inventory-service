package aurora.supply_wok.platform.inventory.application.internal.eventhandlers;

import aurora.supply_wok.platform.inventory.domain.model.events.SupplyMinimumStockReachedEvent;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Publishes inventory low-stock events as integration events.
 */
@Service
public class SupplyMinimumStockReachedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public SupplyMinimumStockReachedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(SupplyMinimumStockReachedEvent event) {
        eventPublisher.publishEvent(new SupplyMinimumStockReachedIntegrationEvent(
                event.supplyId(),
                event.name(),
                event.currentStock(),
                event.minimumStockLevel()
        ));
    }
}

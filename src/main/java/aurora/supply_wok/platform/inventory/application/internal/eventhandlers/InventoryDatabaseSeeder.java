package aurora.supply_wok.platform.inventory.application.internal.eventhandlers;

import aurora.supply_wok.platform.inventory.application.commandservices.SupplyCommandService;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryDatabaseSeeder {
    private final SupplyRepository supplyRepository;
    private final SupplyCommandService supplyCommandService;

    public InventoryDatabaseSeeder(SupplyRepository supplyRepository, SupplyCommandService supplyCommandService) {
        this.supplyRepository = supplyRepository;
        this.supplyCommandService = supplyCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        if (supplyRepository.findAll().isEmpty()) {
            log.info("Seeding inventory supplies...");

            supplyCommandService.handle(new CreateSupplyCommand(
                    "Tomate",
                    EUnitOfMeasure.Kilograms,
                    50,
                    10,
                    "Verduras"
            ));

            supplyCommandService.handle(new CreateSupplyCommand(
                    "Cebolla",
                    EUnitOfMeasure.Kilograms,
                    30,
                    5,
                    "Verduras"
            ));

            log.info("Inventory supplies seeded successfully.");
        }
    }
}

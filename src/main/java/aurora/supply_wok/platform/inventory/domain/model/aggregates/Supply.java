package aurora.supply_wok.platform.inventory.domain.model.aggregates;

import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyCreatedEvent;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyMinimumStockReachedEvent;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyStockChangedEvent;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyUpdatedEvent;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Supply aggregate root.
 */
@Getter
public class Supply extends AbstractDomainAggregateRoot<Supply> {

    @Setter
    private Long id;

    private String name;
    private EUnitOfMeasure unitOfMeasure;
    private int currentStock;
    private int minimumStockLevel;
    private String category;

    public Supply() {
        this.name = "";
        this.unitOfMeasure = EUnitOfMeasure.Units;
        this.currentStock = 0;
        this.minimumStockLevel = 0;
        this.category = "";
    }

    public Supply(String name, EUnitOfMeasure unitOfMeasure, int currentStock, int minimumStockLevel, String category) {
        this();
        validateCoreData(name, minimumStockLevel, category);
        if (currentStock < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative.");
        }

        this.name = name.trim();
        this.unitOfMeasure = unitOfMeasure;
        this.currentStock = currentStock;
        this.minimumStockLevel = minimumStockLevel;
        this.category = category.trim();
    }

    public Supply(CreateSupplyCommand command) {
        this(
                command.name(),
                command.unitOfMeasure(),
                command.currentStock(),
                command.minimumStockLevel(),
                command.category()
        );
    }

    public void update(String name, EUnitOfMeasure unitOfMeasure, Integer currentStock, int minimumStockLevel, String category) {
        validateCoreData(name, minimumStockLevel, category);
        if (currentStock != null && currentStock < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative.");
        }

        this.name = name.trim();
        this.unitOfMeasure = unitOfMeasure;
        if (currentStock != null) {
            this.currentStock = currentStock;
        }
        this.minimumStockLevel = minimumStockLevel;
        this.category = category.trim();
        registerDomainEvent(SupplyUpdatedEvent.from(this));
        if (isAtOrBelowMinimumStock()) {
            registerDomainEvent(SupplyMinimumStockReachedEvent.from(this));
        }
    }

    public void increaseStock(int amount) {
        validateMovementAmount(amount);
        currentStock += amount;
    }

    public void decreaseStock(int amount) {
        validateMovementAmount(amount);
        if (currentStock - amount < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative.");
        }

        currentStock -= amount;
    }

    public void applyMovement(EMovementType type, int amount) {
        switch (type) {
            case Entry -> increaseStock(amount);
            case Exit -> decreaseStock(amount);
            default -> throw new IllegalArgumentException("Unsupported movement type.");
        }
        registerDomainEvent(SupplyStockChangedEvent.from(this, type, amount));
        if (isAtOrBelowMinimumStock()) {
            registerDomainEvent(SupplyMinimumStockReachedEvent.from(this));
        }
    }

    public void onCreated() {
        registerDomainEvent(SupplyCreatedEvent.from(this));
        if (isAtOrBelowMinimumStock()) {
            registerDomainEvent(SupplyMinimumStockReachedEvent.from(this));
        }
    }

    private boolean isAtOrBelowMinimumStock() {
        return currentStock <= minimumStockLevel;
    }

    private static void validateCoreData(String name, int minimumStockLevel, String category) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Supply name cannot be empty.");
        }
        if (minimumStockLevel < 0) {
            throw new IllegalArgumentException("Minimum stock level cannot be negative.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Supply category cannot be empty.");
        }
    }

    private static void validateMovementAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Movement amount must be greater than zero.");
        }
    }
}

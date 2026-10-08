package aurora.supply_wok.platform.inventory.domain.model.aggregates;

import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyCreatedEvent;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyMinimumStockReachedEvent;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyStockChangedEvent;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyUpdatedEvent;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SupplyTests {

    @Test
    void defaultConstructor_whenInstantiated_initializesDefaultValues() {
        // Arrange & Act
        var supply = new Supply();

        // Assert
        assertThat(supply.getName()).isEmpty();
        assertThat(supply.getUnitOfMeasure()).isEqualTo(EUnitOfMeasure.Units);
        assertThat(supply.getCurrentStock()).isZero();
        assertThat(supply.getMinimumStockLevel()).isZero();
        assertThat(supply.getCategory()).isEmpty();
    }

    @Test
    void constructor_withValidArguments_initializesFieldsAndTrimsStrings() {
        // Arrange
        String name = "  Jasmine Rice  ";
        String category = "  Grains  ";

        // Act
        var supply = new Supply(name, EUnitOfMeasure.Kilograms, 50, 10, category);

        // Assert
        assertThat(supply.getName()).isEqualTo("Jasmine Rice");
        assertThat(supply.getCategory()).isEqualTo("Grains");
        assertThat(supply.getUnitOfMeasure()).isEqualTo(EUnitOfMeasure.Kilograms);
        assertThat(supply.getCurrentStock()).isEqualTo(50);
        assertThat(supply.getMinimumStockLevel()).isEqualTo(10);
    }

    @Test
    void constructor_fromCreateSupplyCommand_initializesCorrectly() {
        // Arrange
        var command = new CreateSupplyCommand("Soy Sauce", EUnitOfMeasure.Liters, 20, 5, "Sauces");

        // Act
        var supply = new Supply(command);

        // Assert
        assertThat(supply.getName()).isEqualTo("Soy Sauce");
        assertThat(supply.getUnitOfMeasure()).isEqualTo(EUnitOfMeasure.Liters);
        assertThat(supply.getCurrentStock()).isEqualTo(20);
        assertThat(supply.getMinimumStockLevel()).isEqualTo(5);
        assertThat(supply.getCategory()).isEqualTo("Sauces");
    }

    @Test
    void constructor_whenNameIsBlank_throwsIllegalArgumentException() {
        // Arrange
        String blankName = "   ";

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new Supply(blankName, EUnitOfMeasure.Units, 10, 5, "Category"));
        assertThat(exception.getMessage()).isEqualTo("Supply name cannot be empty.");
    }

    @Test
    void constructor_whenNameIsNull_throwsIllegalArgumentException() {
        // Arrange
        String nullName = null;

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new Supply(nullName, EUnitOfMeasure.Units, 10, 5, "Category"));
        assertThat(exception.getMessage()).isEqualTo("Supply name cannot be empty.");
    }

    @Test
    void constructor_whenMinimumStockIsNegative_throwsIllegalArgumentException() {
        // Arrange
        int negativeMinimumStock = -1;

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new Supply("Sugar", EUnitOfMeasure.Kilograms, 10, negativeMinimumStock, "Category"));
        assertThat(exception.getMessage()).isEqualTo("Minimum stock level cannot be negative.");
    }

    @Test
    void constructor_whenCategoryIsBlank_throwsIllegalArgumentException() {
        // Arrange
        String blankCategory = "\t ";

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new Supply("Salt", EUnitOfMeasure.Grams, 10, 2, blankCategory));
        assertThat(exception.getMessage()).isEqualTo("Supply category cannot be empty.");
    }

    @Test
    void constructor_whenCurrentStockIsNegative_throwsIllegalArgumentException() {
        // Arrange
        int negativeStock = -5;

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new Supply("Pepper", EUnitOfMeasure.Grams, negativeStock, 2, "Spices"));
        assertThat(exception.getMessage()).isEqualTo("Current stock cannot be negative.");
    }

    @Test
    void onCreated_whenCurrentStockAboveMinimum_registersOnlySupplyCreatedEvent() {
        // Arrange
        var supply = new Supply("Flour", EUnitOfMeasure.Kilograms, 20, 5, "Baking");

        // Act
        supply.onCreated();

        // Assert
        var events = supply.domainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.iterator().next()).isInstanceOf(SupplyCreatedEvent.class);
    }

    @Test
    void onCreated_whenCurrentStockAtOrBelowMinimum_registersCreatedAndMinimumStockReachedEvents() {
        // Arrange
        var supply = new Supply("Flour", EUnitOfMeasure.Kilograms, 4, 5, "Baking");

        // Act
        supply.onCreated();

        // Assert
        var events = supply.domainEvents();
        assertThat(events).hasSize(2);
        assertThat(events).anyMatch(e -> e instanceof SupplyCreatedEvent);
        assertThat(events).anyMatch(e -> e instanceof SupplyMinimumStockReachedEvent);
    }

    @Test
    void increaseStock_withPositiveAmount_increasesStock() {
        // Arrange
        var supply = new Supply("Garlic", EUnitOfMeasure.Kilograms, 10, 5, "Produce");

        // Act
        supply.increaseStock(15);

        // Assert
        assertThat(supply.getCurrentStock()).isEqualTo(25);
    }

    @Test
    void increaseStock_withZeroOrNegativeAmount_throwsIllegalArgumentException() {
        // Arrange
        var supply = new Supply("Garlic", EUnitOfMeasure.Kilograms, 10, 5, "Produce");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> supply.increaseStock(0));
        assertThat(exception.getMessage()).isEqualTo("Movement amount must be greater than zero.");
    }

    @Test
    void decreaseStock_withValidAmount_decreasesStock() {
        // Arrange
        var supply = new Supply("Onion", EUnitOfMeasure.Kilograms, 20, 5, "Produce");

        // Act
        supply.decreaseStock(7);

        // Assert
        assertThat(supply.getCurrentStock()).isEqualTo(13);
    }

    @Test
    void decreaseStock_withAmountExceedingStock_throwsIllegalArgumentException() {
        // Arrange
        var supply = new Supply("Onion", EUnitOfMeasure.Kilograms, 10, 5, "Produce");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> supply.decreaseStock(15));
        assertThat(exception.getMessage()).isEqualTo("Current stock cannot be negative.");
    }

    @Test
    void decreaseStock_withZeroOrNegativeAmount_throwsIllegalArgumentException() {
        // Arrange
        var supply = new Supply("Onion", EUnitOfMeasure.Kilograms, 10, 5, "Produce");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> supply.decreaseStock(-2));
        assertThat(exception.getMessage()).isEqualTo("Movement amount must be greater than zero.");
    }

    @Test
    void applyMovement_whenEntryType_increasesStockAndRegistersEvent() {
        // Arrange
        var supply = new Supply("Tofu", EUnitOfMeasure.Units, 10, 5, "Proteins");

        // Act
        supply.applyMovement(EMovementType.Entry, 10);

        // Assert
        assertThat(supply.getCurrentStock()).isEqualTo(20);
        assertThat(supply.domainEvents()).hasSize(1);
        var event = (SupplyStockChangedEvent) supply.domainEvents().iterator().next();
        assertThat(event.movementType()).isEqualTo(EMovementType.Entry);
        assertThat(event.amount()).isEqualTo(10);
        assertThat(event.currentStock()).isEqualTo(20);
    }

    @Test
    void applyMovement_whenExitTypeWithoutReachingMinimum_decreasesStockAndRegistersStockChangedEvent() {
        // Arrange
        var supply = new Supply("Tofu", EUnitOfMeasure.Units, 20, 5, "Proteins");

        // Act
        supply.applyMovement(EMovementType.Exit, 5);

        // Assert
        assertThat(supply.getCurrentStock()).isEqualTo(15);
        assertThat(supply.domainEvents()).hasSize(1);
        assertThat(supply.domainEvents().iterator().next()).isInstanceOf(SupplyStockChangedEvent.class);
    }

    @Test
    void applyMovement_whenExitCausesStockToReachOrDropBelowMinimum_registersMinimumStockReachedEvent() {
        // Arrange
        var supply = new Supply("Tofu", EUnitOfMeasure.Units, 20, 10, "Proteins");

        // Act
        supply.applyMovement(EMovementType.Exit, 12);

        // Assert
        assertThat(supply.getCurrentStock()).isEqualTo(8);
        var events = supply.domainEvents();
        assertThat(events).hasSize(2);
        assertThat(events).anyMatch(e -> e instanceof SupplyStockChangedEvent);
        assertThat(events).anyMatch(e -> e instanceof SupplyMinimumStockReachedEvent);
    }

    @Test
    void update_withValidDataAndStockAboveMinimum_updatesFieldsAndRegistersUpdatedEvent() {
        // Arrange
        var supply = new Supply("Sesame Oil", EUnitOfMeasure.Liters, 10, 2, "Oils");

        // Act
        supply.update("Toasted Sesame Oil", EUnitOfMeasure.Liters, 12, 3, "Premium Oils");

        // Assert
        assertThat(supply.getName()).isEqualTo("Toasted Sesame Oil");
        assertThat(supply.getCurrentStock()).isEqualTo(12);
        assertThat(supply.getMinimumStockLevel()).isEqualTo(3);
        assertThat(supply.getCategory()).isEqualTo("Premium Oils");
        assertThat(supply.domainEvents()).hasSize(1);
        assertThat(supply.domainEvents().iterator().next()).isInstanceOf(SupplyUpdatedEvent.class);
    }

    @Test
    void update_whenNullCurrentStockProvided_retainsExistingStock() {
        // Arrange
        var supply = new Supply("Sesame Oil", EUnitOfMeasure.Liters, 15, 2, "Oils");

        // Act
        supply.update("Sesame Oil", EUnitOfMeasure.Liters, null, 2, "Oils");

        // Assert
        assertThat(supply.getCurrentStock()).isEqualTo(15);
    }

    @Test
    void update_whenStockBecomesAtOrBelowMinimum_registersMinimumStockReachedEvent() {
        // Arrange
        var supply = new Supply("Sesame Oil", EUnitOfMeasure.Liters, 10, 2, "Oils");

        // Act
        supply.update("Sesame Oil", EUnitOfMeasure.Liters, 2, 5, "Oils");

        // Assert
        var events = supply.domainEvents();
        assertThat(events).hasSize(2);
        assertThat(events).anyMatch(e -> e instanceof SupplyUpdatedEvent);
        assertThat(events).anyMatch(e -> e instanceof SupplyMinimumStockReachedEvent);
    }

    @Test
    void update_whenCurrentStockNegative_throwsIllegalArgumentException() {
        // Arrange
        var supply = new Supply("Sesame Oil", EUnitOfMeasure.Liters, 10, 2, "Oils");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                supply.update("Sesame Oil", EUnitOfMeasure.Liters, -1, 2, "Oils"));
        assertThat(exception.getMessage()).isEqualTo("Current stock cannot be negative.");
    }

    @Test
    void update_whenNameBlank_throwsIllegalArgumentException() {
        // Arrange
        var supply = new Supply("Sesame Oil", EUnitOfMeasure.Liters, 10, 2, "Oils");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                supply.update("  ", EUnitOfMeasure.Liters, 5, 2, "Oils"));
        assertThat(exception.getMessage()).isEqualTo("Supply name cannot be empty.");
    }
}

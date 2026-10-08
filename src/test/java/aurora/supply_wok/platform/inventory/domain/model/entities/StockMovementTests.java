package aurora.supply_wok.platform.inventory.domain.model.entities;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockMovementTests {

    @Test
    void defaultConstructor_whenInstantiated_initializesDefaultValues() {
        // Arrange & Act
        var movement = new StockMovement();

        // Assert
        assertThat(movement.getSupplyId()).isZero();
        assertThat(movement.getType()).isEqualTo(EMovementType.Entry);
        assertThat(movement.getAmount()).isZero();
        assertThat(movement.getDate()).isNotNull();
        assertThat(movement.getReason()).isEmpty();
    }

    @Test
    void constructor_withValidArguments_initializesPropertiesAndTrimsReason() {
        // Arrange
        Long supplyId = 42L;
        EMovementType type = EMovementType.Exit;
        int amount = 15;
        LocalDateTime date = LocalDateTime.of(2026, 1, 15, 10, 30);
        String reason = "  Kitchen daily usage  ";

        // Act
        var movement = new StockMovement(supplyId, type, amount, date, reason);

        // Assert
        assertThat(movement.getSupplyId()).isEqualTo(42L);
        assertThat(movement.getType()).isEqualTo(EMovementType.Exit);
        assertThat(movement.getAmount()).isEqualTo(15);
        assertThat(movement.getDate()).isEqualTo(date);
        assertThat(movement.getReason()).isEqualTo("Kitchen daily usage");
    }

    @Test
    void constructor_whenDateIsNull_assignsCurrentDate() {
        // Arrange
        Long supplyId = 10L;

        // Act
        var movement = new StockMovement(supplyId, EMovementType.Entry, 5, null, "Restock");

        // Assert
        assertThat(movement.getDate()).isNotNull();
    }

    @Test
    void constructor_whenSupplyIdIsNull_throwsIllegalArgumentException() {
        // Arrange
        Long nullSupplyId = null;

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new StockMovement(nullSupplyId, EMovementType.Entry, 10, LocalDateTime.now(), "Restock"));
        assertThat(exception.getMessage()).isEqualTo("Supply id must be greater than zero.");
    }

    @Test
    void constructor_whenSupplyIdIsZeroOrNegative_throwsIllegalArgumentException() {
        // Arrange
        Long zeroSupplyId = 0L;

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new StockMovement(zeroSupplyId, EMovementType.Entry, 10, LocalDateTime.now(), "Restock"));
        assertThat(exception.getMessage()).isEqualTo("Supply id must be greater than zero.");
    }

    @Test
    void constructor_whenAmountIsZeroOrNegative_throwsIllegalArgumentException() {
        // Arrange
        int invalidAmount = 0;

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new StockMovement(1L, EMovementType.Entry, invalidAmount, LocalDateTime.now(), "Restock"));
        assertThat(exception.getMessage()).isEqualTo("Movement amount must be greater than zero.");
    }

    @Test
    void constructor_whenReasonIsBlankOrNull_throwsIllegalArgumentException() {
        // Arrange
        String blankReason = "   ";

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new StockMovement(1L, EMovementType.Entry, 10, LocalDateTime.now(), blankReason));
        assertThat(exception.getMessage()).isEqualTo("Movement reason cannot be empty.");
    }
}

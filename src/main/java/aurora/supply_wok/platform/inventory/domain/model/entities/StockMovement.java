package aurora.supply_wok.platform.inventory.domain.model.entities;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stock movement domain entity.
 */
@Getter
public class StockMovement {

    @Setter
    private Long id;

    private Long supplyId;
    private EMovementType type;
    private int amount;
    private LocalDateTime date;
    private String reason;

    public StockMovement() {
        this.supplyId = 0L;
        this.type = EMovementType.Entry;
        this.amount = 0;
        this.date = LocalDateTime.now();
        this.reason = "";
    }

    public StockMovement(Long supplyId, EMovementType type, int amount, LocalDateTime date, String reason) {
        this();
        if (supplyId == null || supplyId <= 0) {
            throw new IllegalArgumentException("Supply id must be greater than zero.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Movement amount must be greater than zero.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Movement reason cannot be empty.");
        }

        this.supplyId = supplyId;
        this.type = type;
        this.amount = amount;
        this.date = date == null ? LocalDateTime.now() : date;
        this.reason = reason.trim();
    }
}

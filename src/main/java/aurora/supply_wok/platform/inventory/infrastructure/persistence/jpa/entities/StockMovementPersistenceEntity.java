package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA persistence entity for stock movements.
 */
@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
public class StockMovementPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supply_id", nullable = false)
    private SupplyPersistenceEntity supply;

    @Column(name = "supply_id", nullable = false, insertable = false, updatable = false)
    private Long supplyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EMovementType type;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 255)
    private String reason;
}

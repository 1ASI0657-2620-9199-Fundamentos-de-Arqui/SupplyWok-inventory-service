package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for supplies.
 */
@Entity
@Table(name = "supplies")
@Getter
@Setter
@NoArgsConstructor
public class SupplyPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private EUnitOfMeasure unitOfMeasure;

    @Column(name = "current_stock", nullable = false)
    private int currentStock;

    @Column(name = "minimum_stock_level", nullable = false)
    private int minimumStockLevel;

    @Column(nullable = false, length = 80)
    private String category;

    @OneToMany(mappedBy = "supply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockMovementPersistenceEntity> stockMovements = new ArrayList<>();
}

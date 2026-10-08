package aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.adapters;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.assemblers.StockMovementPersistenceAssembler;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.assemblers.SupplyPersistenceAssembler;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.entities.SupplyPersistenceEntity;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.repositories.StockMovementPersistenceRepository;
import aurora.supply_wok.platform.inventory.infrastructure.persistence.jpa.repositories.SupplyPersistenceRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the inventory repository port with Spring Data JPA.
 */
@Repository
public class SupplyRepositoryImpl implements SupplyRepository {

    private final SupplyPersistenceRepository supplyPersistenceRepository;
    private final StockMovementPersistenceRepository stockMovementPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public SupplyRepositoryImpl(SupplyPersistenceRepository supplyPersistenceRepository,
                                StockMovementPersistenceRepository stockMovementPersistenceRepository,
                                DomainEventPublisher domainEventPublisher) {
        this.supplyPersistenceRepository = supplyPersistenceRepository;
        this.stockMovementPersistenceRepository = stockMovementPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public List<Supply> findAll() {
        return supplyPersistenceRepository.findAllByOrderByIdAsc()
                .stream()
                .map(SupplyPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Supply> findById(Long id) {
        return supplyPersistenceRepository.findById(id)
                .map(SupplyPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Supply save(Supply supply) {
        var isNew = supply.getId() == null;
        SupplyPersistenceEntity entityToSave;
        if (isNew) {
            entityToSave = SupplyPersistenceAssembler.toPersistenceFromDomain(supply);
        } else {
            entityToSave = supplyPersistenceRepository.findById(supply.getId())
                    .orElseGet(() -> SupplyPersistenceAssembler.toPersistenceFromDomain(supply));
            entityToSave.setName(supply.getName());
            entityToSave.setUnitOfMeasure(supply.getUnitOfMeasure());
            entityToSave.setCurrentStock(supply.getCurrentStock());
            entityToSave.setMinimumStockLevel(supply.getMinimumStockLevel());
            entityToSave.setCategory(supply.getCategory());
        }
        var saved = supplyPersistenceRepository.save(entityToSave);
        if (!isNew) {
            domainEventPublisher.publishAndClear(supply);
        }

        var savedSupply = SupplyPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedSupply.onCreated();
            domainEventPublisher.publishAndClear(savedSupply);
        }
        return savedSupply;
    }

    @Override
    public void delete(Supply supply) {
        if (supply.getId() != null) {
            supplyPersistenceRepository.deleteById(supply.getId());
        }
    }

    @Override
    public long getTotalCurrentStock() {
        return supplyPersistenceRepository.sumCurrentStock();
    }

    @Override
    public StockMovement saveStockMovement(StockMovement stockMovement) {
        var supply = supplyPersistenceRepository.findById(stockMovement.getSupplyId())
                .orElseThrow(() -> new IllegalArgumentException("Supply not found."));
        var saved = stockMovementPersistenceRepository.save(
                StockMovementPersistenceAssembler.toPersistenceFromDomain(stockMovement, supply)
        );
        return StockMovementPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public List<StockMovement> findAllStockMovements() {
        return stockMovementPersistenceRepository.findAllByOrderByDateDesc()
                .stream()
                .map(StockMovementPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<StockMovement> findStockMovementById(Long id) {
        return stockMovementPersistenceRepository.findById(id)
                .map(StockMovementPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<StockMovement> findStockMovementsBySupplyId(Long supplyId) {
        return stockMovementPersistenceRepository.findAllBySupplyIdOrderByDateDesc(supplyId)
                .stream()
                .map(StockMovementPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}

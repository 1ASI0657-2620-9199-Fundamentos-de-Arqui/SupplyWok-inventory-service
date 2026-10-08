package aurora.supply_wok.platform.inventory.domain.model;

/**
 * Inventory error codes aligned with the source bounded context.
 */
public enum InventoryError {
    NONE,
    SUPPLY_NOT_FOUND,
    STOCK_MOVEMENT_NOT_FOUND,
    INVALID_DATA,
    INSUFFICIENT_STOCK,
    OPERATION_CANCELLED,
    DATABASE_ERROR,
    INTERNAL_SERVER_ERROR
}

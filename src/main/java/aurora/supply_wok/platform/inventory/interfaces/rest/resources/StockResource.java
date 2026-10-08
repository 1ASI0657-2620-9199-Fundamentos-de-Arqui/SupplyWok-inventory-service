package aurora.supply_wok.platform.inventory.interfaces.rest.resources;

public record StockResource(
        String stockStatus,
        double maxLevel,
        double minLevel,
        double currentStock
) {}

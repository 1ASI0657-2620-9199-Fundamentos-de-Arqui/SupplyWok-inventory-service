package aurora.supply_wok.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Supply response resource.
 */
@Schema(
        name = "SupplyResponse",
        description = "Supply item response",
        example = """
                {"id": 1, "name": "Rice", "unitOfMeasure": "Kilograms", "currentStock": 80, "minimumStockLevel": 20, "category": "Grains"}
                """
)
public record SupplyResource(
        @Schema(description = "Supply unique identifier", example = "1")
        Long id,

        @Schema(description = "Supply display name", example = "Rice")
        String name,

        @Schema(description = "Supply unit of measure", example = "Kilograms")
        String unitOfMeasure,

        @Schema(description = "Current stock available", example = "80")
        int currentStock,

        @Schema(description = "Minimum stock threshold", example = "20")
        int minimumStockLevel,

        @Schema(description = "Supply category", example = "Grains")
        String category
) {
}

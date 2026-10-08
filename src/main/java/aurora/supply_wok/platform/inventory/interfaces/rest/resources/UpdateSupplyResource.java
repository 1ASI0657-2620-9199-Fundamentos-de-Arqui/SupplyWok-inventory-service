package aurora.supply_wok.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for updating a supply item.
 */
@Schema(
        name = "UpdateSupplyRequest",
        description = "Request payload for updating a supply item",
        example = """
                {"name": "Rice", "unitOfMeasure": "Kilograms", "currentStock": 80, "minimumStockLevel": 25, "category": "Grains"}
                """
)
public record UpdateSupplyResource(
        @NotBlank
        @Schema(description = "Supply display name", example = "Rice", minLength = 1, maxLength = 100)
        String name,

        @NotBlank
        @Schema(description = "Supply unit of measure", example = "Kilograms", allowableValues = {"Kilograms", "Liters", "Units", "Grams"})
        String unitOfMeasure,

        @Min(0)
        @Schema(description = "Current stock quantity", example = "80")
        Integer currentStock,

        @Min(0)
        @Schema(description = "Minimum stock threshold", example = "25")
        int minimumStockLevel,

        @NotBlank
        @Schema(description = "Supply category", example = "Grains", minLength = 1, maxLength = 80)
        String category
) {
}

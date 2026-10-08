package aurora.supply_wok.platform.inventory.interfaces.rest;

import aurora.supply_wok.platform.inventory.application.commandservices.SupplyCommandService;
import aurora.supply_wok.platform.inventory.application.queryservices.SupplyQueryService;
import aurora.supply_wok.platform.inventory.domain.model.commands.DeleteSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetAllSuppliesQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetSupplyByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetTotalSupplyStockQuery;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.CreateSupplyResource;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.SupplyResource;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.UpdateSupplyResource;
import aurora.supply_wok.platform.inventory.interfaces.rest.transform.CreateSupplyCommandFromResourceAssembler;
import aurora.supply_wok.platform.inventory.interfaces.rest.transform.SupplyResourceFromEntityAssembler;
import aurora.supply_wok.platform.inventory.interfaces.rest.transform.UpdateSupplyCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes supply endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/supplies", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supplies", description = "Supply management endpoints")
public class SuppliesController {

    private final SupplyCommandService supplyCommandService;
    private final SupplyQueryService supplyQueryService;

    public SuppliesController(SupplyCommandService supplyCommandService, SupplyQueryService supplyQueryService) {
        this.supplyCommandService = supplyCommandService;
        this.supplyQueryService = supplyQueryService;
    }

    @PostMapping
    @Operation(summary = "Create supply", description = "Creates a new supply item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supply created successfully",
                    content = @Content(schema = @Schema(implementation = SupplyResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<SupplyResource> createSupply(@Valid @RequestBody CreateSupplyResource resource) {
        try {
            var command = CreateSupplyCommandFromResourceAssembler.toCommandFromResource(resource);
            var supply = supplyCommandService.handle(command);
            return new ResponseEntity<>(SupplyResourceFromEntityAssembler.toResourceFromEntity(supply), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all supplies", description = "Retrieves all supply items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplies retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SupplyResource.class))))
    })
    public ResponseEntity<List<SupplyResource>> getAllSupplies() {
        var supplies = supplyQueryService.handle(new GetAllSuppliesQuery())
                .stream()
                .map(SupplyResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(supplies);
    }

    @GetMapping("/total-stock")
    @Operation(summary = "Get total supply stock", description = "Retrieves the total current stock across all supplies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total stock retrieved successfully")
    })
    public ResponseEntity<Map<String, Long>> getTotalSupplyStock() {
        var total = supplyQueryService.handle(new GetTotalSupplyStockQuery());
        return ResponseEntity.ok(Map.of("totalCurrentStock", total));
    }

    @GetMapping("/{supplyId}")
    @Operation(summary = "Get supply by id", description = "Retrieves a supply item by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supply found",
                    content = @Content(schema = @Schema(implementation = SupplyResource.class))),
            @ApiResponse(responseCode = "404", description = "Supply not found")
    })
    public ResponseEntity<SupplyResource> getSupplyById(@PathVariable Long supplyId) {
        var supply = supplyQueryService.handle(new GetSupplyByIdQuery(supplyId));
        if (supply.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(SupplyResourceFromEntityAssembler.toResourceFromEntity(supply.get()));
    }

    @PutMapping("/{supplyId}")
    @Operation(summary = "Update supply", description = "Updates an existing supply item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supply updated successfully",
                    content = @Content(schema = @Schema(implementation = SupplyResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Supply not found")
    })
    public ResponseEntity<SupplyResource> updateSupply(@PathVariable Long supplyId,
                                                       @Valid @RequestBody UpdateSupplyResource resource) {
        try {
            var command = UpdateSupplyCommandFromResourceAssembler.toCommandFromResource(supplyId, resource);
            var supply = supplyCommandService.handle(command);
            if (supply.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(SupplyResourceFromEntityAssembler.toResourceFromEntity(supply.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{supplyId}")
    @Operation(summary = "Delete supply", description = "Deletes an existing supply item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supply deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supply not found")
    })
    public ResponseEntity<Void> deleteSupply(@PathVariable Long supplyId) {
        var deleted = supplyCommandService.handle(new DeleteSupplyCommand(supplyId));
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}

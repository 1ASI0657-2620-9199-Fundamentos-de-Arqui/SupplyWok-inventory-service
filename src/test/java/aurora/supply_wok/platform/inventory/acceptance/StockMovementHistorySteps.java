package aurora.supply_wok.platform.inventory.acceptance;

import aurora.supply_wok.platform.inventory.application.internal.commandservices.StockMovementCommandServiceImpl;
import aurora.supply_wok.platform.inventory.application.internal.commandservices.SupplyCommandServiceImpl;
import aurora.supply_wok.platform.inventory.application.internal.queryservices.StockMovementQueryServiceImpl;
import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateStockMovementCommand;
import aurora.supply_wok.platform.inventory.domain.model.commands.UpdateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.entities.StockMovement;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetStockMovementsBySupplyIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StockMovementHistorySteps {

    private final SupplyRepository supplyRepository = Mockito.mock(SupplyRepository.class);
    private final StockMovementCommandServiceImpl stockMovementCommandService = new StockMovementCommandServiceImpl(supplyRepository);
    private final SupplyCommandServiceImpl supplyCommandService = new SupplyCommandServiceImpl(supplyRepository);
    private final StockMovementQueryServiceImpl stockMovementQueryService = new StockMovementQueryServiceImpl(supplyRepository);

    private Supply currentSupply;
    private Optional<StockMovement> recordedMovement;
    private final List<StockMovement> movementHistory = new ArrayList<>();

    @Given("an existing supply {string} with current stock of {int}")
    public void anExistingSupplyWithCurrentStockOf(String name, int currentStock) {
        currentSupply = new Supply(1L, name, EUnitOfMeasure.Units, currentStock, 5, "Pasta");
        currentSupply.setId(30L);

        when(supplyRepository.findById(30L)).thenReturn(Optional.of(currentSupply));
        when(supplyRepository.save(any(Supply.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(supplyRepository.saveStockMovement(any(StockMovement.class))).thenAnswer(invocation -> {
            StockMovement movement = invocation.getArgument(0);
            movement.setId(100L + movementHistory.size());
            movementHistory.add(movement);
            return movement;
        });
    }

    @When("an entry movement of {int} is recorded with reason {string}")
    public void anEntryMovementOfIsRecordedWithReason(int amount, String reason) {
        var command = new CreateStockMovementCommand(currentSupply.getId(), EMovementType.Entry, amount, LocalDateTime.now(), reason);
        recordedMovement = stockMovementCommandService.handle(command);
    }

    @When("an exit movement of {int} is recorded with reason {string}")
    public void anExitMovementOfIsRecordedWithReason(int amount, String reason) {
        var command = new CreateStockMovementCommand(currentSupply.getId(), EMovementType.Exit, amount, LocalDateTime.now(), reason);
        recordedMovement = stockMovementCommandService.handle(command);
    }

    @Then("the supply current stock should be updated to {int}")
    public void theSupplyCurrentStockShouldBeUpdatedTo(int expectedStock) {
        assertThat(currentSupply.getCurrentStock()).isEqualTo(expectedStock);
    }

    @Then("the stock movement should be saved with type {string} and amount {int}")
    public void theStockMovementShouldBeSavedWithTypeAndAmount(String typeStr, int expectedAmount) {
        assertThat(recordedMovement).isPresent();
        EMovementType expectedType = EMovementType.valueOf(typeStr);
        assertThat(recordedMovement.get().getType()).isEqualTo(expectedType);
        assertThat(recordedMovement.get().getAmount()).isEqualTo(expectedAmount);
    }

    @Given("an existing supply {string} with recorded stock movements")
    public void anExistingSupplyWithRecordedStockMovements(String name) {
        currentSupply = new Supply(1L, name, EUnitOfMeasure.Units, 25, 5, "Proteins");
        currentSupply.setId(40L);

        var m1 = new StockMovement(40L, EMovementType.Entry, 20, LocalDateTime.now().minusDays(2), "Initial stock");
        m1.setId(101L);
        var m2 = new StockMovement(40L, EMovementType.Entry, 5, LocalDateTime.now().minusDays(1), "Batch delivery");
        m2.setId(102L);
        movementHistory.add(m1);
        movementHistory.add(m2);

        when(supplyRepository.findById(40L)).thenReturn(Optional.of(currentSupply));
        when(supplyRepository.findByIdAndRestaurantId(40L, 1L)).thenReturn(Optional.of(currentSupply));
        when(supplyRepository.save(any(Supply.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(supplyRepository.findStockMovementsBySupplyId(40L)).thenAnswer(inv -> List.copyOf(movementHistory));
    }

    @When("the supply details are updated to name {string} and category {string}")
    public void theSupplyDetailsAreUpdatedToNameAndCategory(String newName, String newCategory) {
        var updateCommand = new UpdateSupplyCommand(40L, 1L, newName, currentSupply.getUnitOfMeasure(), currentSupply.getCurrentStock(), currentSupply.getMinimumStockLevel(), newCategory);
        var updated = supplyCommandService.handle(updateCommand);
        assertThat(updated).isPresent();
        currentSupply = updated.get();
    }

    @Then("the supply details should reflect {string} and {string}")
    public void theSupplyDetailsShouldReflectAnd(String expectedName, String expectedCategory) {
        assertThat(currentSupply.getName()).isEqualTo(expectedName);
        assertThat(currentSupply.getCategory()).isEqualTo(expectedCategory);
    }

    @Then("all previously recorded stock movements for the supply remain intact")
    public void allPreviouslyRecordedStockMovementsForTheSupplyRemainIntact() {
        var movements = stockMovementQueryService.handle(new GetStockMovementsBySupplyIdQuery(currentSupply.getId()));
        assertThat(movements).hasSize(2);
        assertThat(movements).extracting(StockMovement::getReason)
                .containsExactly("Initial stock", "Batch delivery");
    }
}

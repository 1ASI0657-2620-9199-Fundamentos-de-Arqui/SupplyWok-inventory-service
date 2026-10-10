package aurora.supply_wok.platform.inventory.acceptance;

import aurora.supply_wok.platform.inventory.application.internal.commandservices.SupplyCommandServiceImpl;
import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.CreateSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.events.SupplyMinimumStockReachedEvent;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EMovementType;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SupplyMinimumStockSteps {

    private final SupplyRepository supplyRepository = Mockito.mock(SupplyRepository.class);
    private final SupplyCommandServiceImpl supplyCommandService = new SupplyCommandServiceImpl(supplyRepository);

    private CreateSupplyCommand createSupplyCommand;
    private Supply createdSupply;
    private Supply existingSupply;

    @Given("a new supply {string} with initial stock of {int} and minimum stock level of {int}")
    public void aNewSupplyWithInitialStockOfAndMinimumStockLevelOf(String name, int initialStock, int minStock) {
        createSupplyCommand = new CreateSupplyCommand(1L, name, EUnitOfMeasure.Units, initialStock, minStock, "Ingredients");
    }

    @When("the supply creation is processed")
    public void theSupplyCreationIsProcessed() {
        when(supplyRepository.save(any(Supply.class))).thenAnswer(invocation -> {
            Supply supply = invocation.getArgument(0);
            supply.setId(10L);
            supply.onCreated();
            return supply;
        });

        createdSupply = supplyCommandService.handle(createSupplyCommand);
    }

    @Then("a minimum stock reached event should be registered for {string}")
    public void aMinimumStockReachedEventShouldBeRegisteredFor(String name) {
        Supply target = createdSupply != null ? createdSupply : existingSupply;
        assertThat(target).isNotNull();
        assertThat(target.domainEvents())
                .anyMatch(event -> event instanceof SupplyMinimumStockReachedEvent minEvent
                        && minEvent.name().equals(name));
    }

    @Then("no minimum stock reached event should be registered")
    public void noMinimumStockReachedEventShouldBeRegistered() {
        Supply target = createdSupply != null ? createdSupply : existingSupply;
        assertThat(target).isNotNull();
        assertThat(target.domainEvents())
                .noneMatch(event -> event instanceof SupplyMinimumStockReachedEvent);
    }

    @Given("an existing supply {string} with current stock of {int} and minimum stock level of {int}")
    public void anExistingSupplyWithCurrentStockOfAndMinimumStockLevelOf(String name, int currentStock, int minStock) {
        existingSupply = new Supply(1L, name, EUnitOfMeasure.Liters, currentStock, minStock, "Oils");
        existingSupply.setId(20L);
        existingSupply.clearDomainEvents();
    }

    @When("a stock exit movement of {int} is applied to {string}")
    public void aStockExitMovementOfIsAppliedTo(int amount, String name) {
        assertThat(existingSupply.getName()).isEqualTo(name);
        existingSupply.applyMovement(EMovementType.Exit, amount);
    }

    @Then("the current stock of {string} should be {int}")
    public void theCurrentStockOfShouldBe(String name, int expectedStock) {
        assertThat(existingSupply.getName()).isEqualTo(name);
        assertThat(existingSupply.getCurrentStock()).isEqualTo(expectedStock);
    }
}

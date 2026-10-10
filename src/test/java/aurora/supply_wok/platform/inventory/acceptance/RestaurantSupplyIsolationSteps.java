package aurora.supply_wok.platform.inventory.acceptance;

import aurora.supply_wok.platform.inventory.application.internal.commandservices.SupplyCommandServiceImpl;
import aurora.supply_wok.platform.inventory.application.internal.queryservices.SupplyQueryServiceImpl;
import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.commands.DeleteSupplyCommand;
import aurora.supply_wok.platform.inventory.domain.model.queries.GetSupplyByIdQuery;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestaurantSupplyIsolationSteps {

    private final SupplyRepository supplyRepository = Mockito.mock(SupplyRepository.class);
    private final SupplyCommandServiceImpl supplyCommandService = new SupplyCommandServiceImpl(supplyRepository);
    private final SupplyQueryServiceImpl supplyQueryService = new SupplyQueryServiceImpl(supplyRepository);

    private Supply storedSupply;
    private Optional<Supply> queryResult;
    private boolean deleteResult;

    @Given("restaurant {long} owns a supply with id {long} and name {string}")
    public void restaurantOwnsASupplyWithIdAndName(Long restaurantId, Long supplyId, String name) {
        storedSupply = new Supply(restaurantId, name, EUnitOfMeasure.Kilograms, 10, 2, "Condiments");
        storedSupply.setId(supplyId);

        when(supplyRepository.findByIdAndRestaurantId(supplyId, restaurantId)).thenReturn(Optional.of(storedSupply));
        when(supplyRepository.findById(supplyId)).thenReturn(Optional.of(storedSupply));
    }

    @When("restaurant {long} attempts to retrieve supply with id {long}")
    public void restaurantAttemptsToRetrieveSupplyWithId(Long requestingRestaurantId, Long supplyId) {
        queryResult = supplyQueryService.handle(new GetSupplyByIdQuery(supplyId, requestingRestaurantId));
    }

    @Then("the supply should not be found")
    public void theSupplyShouldNotBeFound() {
        assertThat(queryResult).isEmpty();
    }

    @When("restaurant {long} attempts to delete supply with id {long}")
    public void restaurantAttemptsToDeleteSupplyWithId(Long requestingRestaurantId, Long supplyId) {
        deleteResult = supplyCommandService.handle(new DeleteSupplyCommand(supplyId, requestingRestaurantId));
    }

    @Then("the deletion should be rejected as not found")
    public void theDeletionShouldBeRejectedAsNotFound() {
        assertThat(deleteResult).isFalse();
    }

    @Then("the supply should still exist in the repository")
    public void theSupplyShouldStillExistInTheRepository() {
        verify(supplyRepository, never()).delete(any(Supply.class));
    }

    @When("restaurant {long} retrieves supply with id {long}")
    public void restaurantRetrievesSupplyWithId(Long requestingRestaurantId, Long supplyId) {
        queryResult = supplyQueryService.handle(new GetSupplyByIdQuery(supplyId, requestingRestaurantId));
    }

    @Then("the supply {string} should be returned successfully")
    public void theSupplyShouldBeReturnedSuccessfully(String expectedName) {
        assertThat(queryResult).isPresent();
        assertThat(queryResult.get().getName()).isEqualTo(expectedName);
    }

    @When("restaurant {long} deletes supply with id {long}")
    public void restaurantDeletesSupplyWithId(Long requestingRestaurantId, Long supplyId) {
        deleteResult = supplyCommandService.handle(new DeleteSupplyCommand(supplyId, requestingRestaurantId));
    }

    @Then("the deletion should succeed")
    public void theDeletionShouldSucceed() {
        assertThat(deleteResult).isTrue();
        verify(supplyRepository).delete(storedSupply);
    }
}

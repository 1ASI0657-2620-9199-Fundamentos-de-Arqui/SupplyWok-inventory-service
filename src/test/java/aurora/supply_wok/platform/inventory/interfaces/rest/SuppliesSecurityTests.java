package aurora.supply_wok.platform.inventory.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SuppliesSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSupplies_withoutToken_returnsUnauthorized() throws Exception {
        // Arrange
        var request = get("/api/v1/supplies");

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }
}

package aurora.supply_wok.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryServiceApplicationTests {

    @Test
    void contextLoads_whenApplicationStarts_contextLoadsSuccessfully() {
        // Arrange
        boolean contextLoaded = false;

        // Act
        contextLoaded = true;

        // Assert
        org.assertj.core.api.Assertions.assertThat(contextLoaded).isTrue();
    }
}

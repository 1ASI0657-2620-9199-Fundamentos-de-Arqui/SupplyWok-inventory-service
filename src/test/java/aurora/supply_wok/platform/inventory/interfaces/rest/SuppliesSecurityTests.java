package aurora.supply_wok.platform.inventory.interfaces.rest;

import aurora.supply_wok.platform.inventory.domain.model.aggregates.Supply;
import aurora.supply_wok.platform.inventory.domain.model.valueobjects.EUnitOfMeasure;
import aurora.supply_wok.platform.inventory.domain.repositories.SupplyRepository;
import aurora.supply_wok.platform.inventory.interfaces.rest.resources.SupplyResource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SuppliesSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupplyRepository supplyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${authorization.jwt.secret}")
    private String jwtSecret;

    private String createToken(Long userId, String email, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    @Test
    void getSupplies_withoutToken_returnsUnauthorized() throws Exception {
        // Arrange
        var request = get("/api/v1/supplies");

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void getSupplies_whenUserHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        String token = createToken(10L, "supplier@ejemplo.com", List.of("SUPPLIER"));
        var request = get("/api/v1/supplies")
                .header("Authorization", "Bearer " + token);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void createSupply_whenUserHasWrongRole_returnsForbidden() throws Exception {
        // Arrange
        String token = createToken(10L, "supplier@ejemplo.com", List.of("SUPPLIER"));
        var payload = """
                {
                    "name": "Bok Choy",
                    "unitOfMeasure": "Kilograms",
                    "currentStock": 20,
                    "minimumStockLevel": 5,
                    "category": "Produce"
                }
                """;
        var request = post("/api/v1/supplies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isForbidden());
    }

    @Test
    void createSupply_whenBodyContainsDifferentRestaurantId_usesTenantIdFromToken() throws Exception {
        // Arrange
        Long callerRestaurantId = 100L;
        String token = createToken(callerRestaurantId, "restaurant100@ejemplo.com", List.of("RESTAURANT"));
        var payload = """
                {
                    "name": "Organic Sesame Seeds",
                    "unitOfMeasure": "Kilograms",
                    "currentStock": 15,
                    "minimumStockLevel": 3,
                    "category": "Seeds",
                    "restaurantId": 999
                }
                """;
        var request = post("/api/v1/supplies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Act
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        SupplyResource created = objectMapper.readValue(result.getResponse().getContentAsString(), SupplyResource.class);
        var supplyInDb = supplyRepository.findById(created.id()).orElseThrow();
        assertThat(supplyInDb.getRestaurantId()).isEqualTo(callerRestaurantId);
        assertThat(supplyInDb.getRestaurantId()).isNotEqualTo(999L);
    }

    @Test
    void getAllSupplies_whenCalledByRestaurant_returnsOnlyCallersResources() throws Exception {
        // Arrange
        Long restaurantA = 200L;
        Long restaurantB = 300L;
        Supply supplyA = supplyRepository.save(new Supply(restaurantA, "Wheat Noodles", EUnitOfMeasure.Units, 20, 5, "Noodles"));
        Supply supplyB = supplyRepository.save(new Supply(restaurantB, "Ramen Noodles", EUnitOfMeasure.Units, 30, 5, "Noodles"));
        String tokenA = createToken(restaurantA, "restaurantA@ejemplo.com", List.of("RESTAURANT"));
        var request = get("/api/v1/supplies")
                .header("Authorization", "Bearer " + tokenA);

        // Act
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        List<SupplyResource> supplies = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<SupplyResource>>() {}
        );
        assertThat(supplies).anyMatch(s -> s.id().equals(supplyA.getId()));
        assertThat(supplies).noneMatch(s -> s.id().equals(supplyB.getId()));
    }

    @Test
    void getSupplyById_whenSupplyBelongsToAnotherRestaurant_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantA = 201L;
        Long restaurantB = 202L;
        Supply supplyA = supplyRepository.save(new Supply(restaurantA, "Soy Sauce Premium", EUnitOfMeasure.Liters, 10, 2, "Sauces"));
        String tokenB = createToken(restaurantB, "restaurantB@ejemplo.com", List.of("RESTAURANT"));
        var request = get("/api/v1/supplies/" + supplyA.getId())
                .header("Authorization", "Bearer " + tokenB);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void updateSupply_whenSupplyBelongsToAnotherRestaurant_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantA = 203L;
        Long restaurantB = 204L;
        Supply supplyA = supplyRepository.save(new Supply(restaurantA, "Rice Vinegar Special", EUnitOfMeasure.Liters, 15, 3, "Sauces"));
        String tokenB = createToken(restaurantB, "restaurantB2@ejemplo.com", List.of("RESTAURANT"));
        var payload = """
                {
                    "name": "Rice Vinegar Tampered",
                    "unitOfMeasure": "Liters",
                    "currentStock": 20,
                    "minimumStockLevel": 4,
                    "category": "Sauces"
                }
                """;
        var request = put("/api/v1/supplies/" + supplyA.getId())
                .header("Authorization", "Bearer " + tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void deleteSupply_whenSupplyBelongsToAnotherRestaurant_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantA = 205L;
        Long restaurantB = 206L;
        Supply supplyA = supplyRepository.save(new Supply(restaurantA, "Sea Salt", EUnitOfMeasure.Kilograms, 10, 2, "Spices"));
        String tokenB = createToken(restaurantB, "restaurantB3@ejemplo.com", List.of("RESTAURANT"));
        var request = delete("/api/v1/supplies/" + supplyA.getId())
                .header("Authorization", "Bearer " + tokenB);

        // Act
        var result = mockMvc.perform(request);

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void getTotalSupplyStock_whenCalledByRestaurant_returnsOnlyCallersTotalStock() throws Exception {
        // Arrange
        Long restaurantA = 207L;
        Long restaurantB = 208L;
        supplyRepository.save(new Supply(restaurantA, "Tempura Flour A", EUnitOfMeasure.Kilograms, 40, 5, "Flour"));
        supplyRepository.save(new Supply(restaurantA, "Bread Flour A", EUnitOfMeasure.Kilograms, 60, 5, "Flour"));
        supplyRepository.save(new Supply(restaurantB, "Corn Flour B", EUnitOfMeasure.Kilograms, 150, 5, "Flour"));
        String tokenA = createToken(restaurantA, "restaurantA4@ejemplo.com", List.of("RESTAURANT"));
        var request = get("/api/v1/supplies/total-stock")
                .header("Authorization", "Bearer " + tokenA);

        // Act
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Map<String, Long> responseMap = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<Map<String, Long>>() {}
        );
        assertThat(responseMap.get("totalCurrentStock")).isEqualTo(100L);
    }
}

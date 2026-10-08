Feature: Supply Stock Movement History
  As a restaurant inventory manager
  I want to update supplies and record every stock entry or exit
  So that I have an accurate history of stock movements

  Scenario: Registering an entry movement updates supply stock and records history
    Given an existing supply "Wheat Noodles" with current stock of 20
    When an entry movement of 15 is recorded with reason "Restock shipment"
    Then the supply current stock should be updated to 35
    And the stock movement should be saved with type "Entry" and amount 15

  Scenario: Registering an exit movement updates supply stock and records history
    Given an existing supply "Wheat Noodles" with current stock of 20
    When an exit movement of 8 is recorded with reason "Kitchen prep"
    Then the supply current stock should be updated to 12
    And the stock movement should be saved with type "Exit" and amount 8

  Scenario: Updating supply details preserves existing stock movement history
    Given an existing supply "Tofu" with recorded stock movements
    When the supply details are updated to name "Firm Tofu" and category "Fresh Proteins"
    Then the supply details should reflect "Firm Tofu" and "Fresh Proteins"
    And all previously recorded stock movements for the supply remain intact

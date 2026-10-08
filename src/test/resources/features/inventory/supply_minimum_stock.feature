Feature: Supply Minimum Stock Monitoring
  As a restaurant inventory manager
  I want to track supply stock levels and detect when items reach their minimum thresholds
  So that I can replenish stock before running out of ingredients

  Scenario: Registering a supply below its minimum stock raises minimum-stock reached event
    Given a new supply "Soy Sauce" with initial stock of 2 and minimum stock level of 5
    When the supply creation is processed
    Then a minimum stock reached event should be registered for "Soy Sauce"

  Scenario: Registering a supply at or above its minimum stock does not raise minimum-stock event
    Given a new supply "Jasmine Rice" with initial stock of 20 and minimum stock level of 10
    When the supply creation is processed
    Then no minimum stock reached event should be registered

  Scenario: Decreasing supply stock below minimum via stock movement raises minimum-stock reached event
    Given an existing supply "Sesame Oil" with current stock of 15 and minimum stock level of 10
    When a stock exit movement of 8 is applied to "Sesame Oil"
    Then the current stock of "Sesame Oil" should be 7
    And a minimum stock reached event should be registered for "Sesame Oil"

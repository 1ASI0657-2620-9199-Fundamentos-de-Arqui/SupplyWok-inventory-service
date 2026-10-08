Feature: Restaurant Supply Isolation
  As a multi-tenant restaurant owner
  I want supply queries and deletions to be strictly isolated by restaurant
  So that a restaurant cannot read, modify, or delete supplies belonging to another restaurant

  Scenario: Restaurant cannot read a supply belonging to another restaurant
    Given restaurant 101 owns a supply with id 10 and name "Special Chili Paste"
    When restaurant 202 attempts to retrieve supply with id 10
    Then the supply should not be found

  Scenario: Restaurant cannot delete a supply belonging to another restaurant
    Given restaurant 101 owns a supply with id 10 and name "Special Chili Paste"
    When restaurant 202 attempts to delete supply with id 10
    Then the deletion should be rejected as not found
    And the supply should still exist in the repository

  Scenario: Restaurant can read and delete its own supply
    Given restaurant 101 owns a supply with id 10 and name "Special Chili Paste"
    When restaurant 101 retrieves supply with id 10
    Then the supply "Special Chili Paste" should be returned successfully
    When restaurant 101 deletes supply with id 10
    Then the deletion should succeed

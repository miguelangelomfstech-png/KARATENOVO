Feature: User API Tests

  Background:
    * url baseUrl
    * def ClientFactory = Java.type('com.api.framework.helpers.ClientFactory')
    * def headers = ClientFactory.getHeaders()
    * configure headers = headers

  Scenario: Get all users and validate schema
    Given path 'users'
    When method get
    Then status 200
    And match each response ==
      """
      {
        "id": "#number",
        "name": "#string",
        "username": "#string",
        "email": "#string",
        "address": "#object",
        "phone": "#string",
        "website": "#string",
        "company": "#object"
      }
      """

  Scenario: Create a new user using a Java POJO
    * def UserRequest = Java.type('com.api.framework.models.UserRequest')
    * def userBuilder = UserRequest.builder().name('Test User').username('testuser').email('test@example.com').build()
    # Serialize POJO to JSON
    # Note: In Karate, simple POJOs work, but for complex interaction we can also rely on JSON
    * def requestBody = 
    """
    {
      "name": "Test User",
      "username": "testuser",
      "email": "test@example.com"
    }
    """
    
    Given path 'users'
    And request requestBody
    When method post
    Then status 201
    And match response.name == 'Test User'
    And match response.id == '#present'

  Scenario: Update an existing user
    Given path 'users', 1
    And request { "name": "Updated Name" }
    When method patch
    Then status 200
    And match response.name == 'Updated Name'

  Scenario: Delete a user
    Given path 'users', 1
    When method delete
    Then status 200

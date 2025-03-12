Feature: Authenticate and Fetch Data from DummyJSON API

  Background:
    * url 'https://dummyjson.com'

  @login
  Scenario: Login and Fetch Data
    Given path '/auth/login'
    And request { username: 'sophiab', password: 'sophiabpass', expiresInMins: 60 }
    And header Content-Type = 'application/json'
    When method post
    Then status 200
    * def authToken = response.accessToken
    * print 'Token from login:', authToken

    Given path '/auth/me'
    And header Authorization = 'Bearer ' + authToken
    And header Content-Type = 'application/json'
    When method get
    Then status 200
    * print response

  @addProducts
  Scenario: Add Products with Title Only
    Given path '/products/add'
    And request { "title": "product"}
    And header Content-Type = 'application/json'
    When method post
    Then status 201
    * print response
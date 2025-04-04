Feature: Fetch Data from DummyJSON API

  Background:
    * url 'https://dummyjson.com'
    * def authResponse = call read('authtest.feature')
    * def authToken = authResponse.authToken
    * print 'Token from Background:', authToken

  @getProducts
  Scenario: Get Products
    Given path '/auth/me'
    And header Authorization = 'Bearer ' + authToken
    And header Content-Type = 'application/json'
    When method get
    Then status 200
    * print response

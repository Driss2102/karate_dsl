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


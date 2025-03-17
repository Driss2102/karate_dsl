Feature: Authenticate with externe data

  Background:
    * url 'https://dummyjson.com'

  Scenario: Login and Fetch Data from params

    * def username = karate.properties['username']
    * def password = karate.properties['password']
    * def expiresInMins = karate.properties['expiresInMins']


    * karate.log('Valeurs reçues - username:', username, 'password:', password, 'expiresInMins:', expiresInMins)

    * def requestBody = {    username: '#(username)',    password: '#(password)',    expiresInMins: '#(expiresInMins)'    }

    * karate.log('Request body:', requestBody)

    Given path '/auth/login'
    And request requestBody
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

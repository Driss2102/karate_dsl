#Feature: Authenticate and Fetch Data from DummyJSON API
#
#  Background:
#    * url 'https://dummyjson.com'
#
#  Scenario Outline: Login and Fetch Data from CSV
#    Given path '/auth/login'
#    And request { username: '<username>', password: '<password>', expiresInMins: <expiresInMins> }
#    And header Content-Type = 'application/json'
#    When method post
#    Then status 200
#    * def authToken = response.accessToken
#    * print 'Token from login:', authToken
#
#    Given path '/auth/me'
#    And header Authorization = 'Bearer ' + authToken
#    And header Content-Type = 'application/json'
#    When method get
#    Then status 200
#    * print response
#
#    Examples:
#      | username  | password     | expiresInMins |
#      | sophiab   | sophiabpass  | 60           |
#      | emilys    | emilyspass   | 120          |
#////////////////////////////////////////////////////////////////////////////////

Feature: Authenticate and Fetch Data from DummyJSON API using CSV data

  Background:
    * url 'https://dummyjson.com'

  Scenario Outline: Login and Fetch Data with multiple users
    Given path '/auth/login'
    And request { username: '<username>', password: '<password>', expiresInMins: <expiresInMins> }

    And header Content-Type = 'application/json'
    When method post
    Then status 200
    * def authToken = response.accessToken
    * print 'Token from login for user <username>:', authToken

    Given path '/auth/me'
    And header Authorization = 'Bearer ' + authToken
    And header Content-Type = 'application/json'
    When method get
    Then status 200

    * print 'Response for user <username> and the file : request.csv:', response


    Examples:
      | karate.read('request.csv') |




Scenario Outline: Login and Fetch Data with multiple users
Given path '/auth/login'
And request { username: '<username>', password: '<password>', expiresInMins: <expiresInMins> }
And header Content-Type = 'application/json'
When method post
Then status 200
* def authToken = response.accessToken
* print 'Token from login for user <username>:', authToken

Given path '/auth/me'
And header Authorization = 'Bearer ' + authToken
And header Content-Type = 'application/json'
When method get
Then status 200

* print 'Response for user <username> from the JSON file:', response

Examples:
| karate.read('request.json') |

Feature: Test GraphQL API for Country Data

  Background:
    * url 'https://countries.trevorblades.com/graphql'
    * header Content-Type = 'application/json'

  @getCountry
  Scenario: Fetch country details using GraphQL query
    Given request { query: "query { country(code: \"MA\") { name native capital emoji currency languages { code name } } }" }
    When method post
    Then status 200
    * def countryData = response.data.country
    * print 'Country Data:', countryData

    # pour valider les donnees de la reponse
    * match countryData.name == "Morocco"
    * match countryData.capital == "Rabat"
    * match countryData.currency == "MAD"
    * match countryData.languages[0].code == "ar"
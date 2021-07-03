#Project
* Kotlin 1.5 + JDK 11
* spring webflux framework
* build : gradle
* Test : JUnit, Mockito, MockWebServer
* Assumption:
  for some names, getting nationality from https://api.nationalize.io/ may get empty list of country or multiple countries
  * [ for empty list, map it to "UNKNOWN" ]
  * [ for multiple countries, select the one with the highest probability ]
* 2 seconds timeout for all API calls  
* IDE: intellij
* run 
  * [./gradlew test --parallel] to run the tests
  * [./gradlew bootRun] to start the application
import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._

class PerformanceTest extends Simulation {

  // Define the Karate protocol (Optional: Configure Gatling metrics)
  val protocol = karateProtocol(
    "/auth/login" -> pauseFor("post" -> 1000), // Simulates a 1s delay between login requests
    "/auth/me" -> pauseFor("get" -> 500) // 500ms delay for fetching user data
  )

  // Define the scenario
  val loginAndFetchData = scenario("Authenticate and Fetch Data")
    .exec(karateFeature("classpath:karate/test.feature")) // Runs the Karate test

  // Configure Gatling Load Simulation
  setUp(
    loginAndFetchData.inject(
      atOnceUsers(10), // Simulate 10 users instantly
      rampUsers(50).during(10) // Ramp up 50 users over 10 seconds
    )
  ).protocols(protocol) // Use Karate protocol
}

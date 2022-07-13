package simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class GetProducts extends Simulation{
  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt
  def environment: String = getProperty("ENVIRONMENT", "staging-dev")
  def ledgerAccountId: String = getProperty("LEDGER_ACCOUNT_ID", "6c4b5b67-862c-438b-b885-c8c7c448e78e")

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  val baseUrlMap = Map(
    "staging-dev" -> "https://mobile-store-gateway-dev.xendit.co",
    "local" -> "http://localhost:3001/"
  )

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrlMap(environment))
    .header("Content-Type", "application/json")
    .header("x-session-token","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXNzaW9uX3V1aWQiOiI1ZDFjOGZlMy1iNjNkLTQ5MmYtOTdhMC1jNmU3ZGE2ZTI0MzEiLCJ1c2VyIjp7ImlkIjoiNjJjYjk5OWRlZjJiMjYwMDIwYzY2MDFiIn0sImJ1c2luZXNzIjp7ImlkIjoiNjJjYjk5OWFlMmZmMjg3NjA4ZTJkMjJmIn0sInVzZXJfYnVzaW5lc3MiOnsiaWQiOiI2MmNiOTk5ZWVmMmIyNjAwMjBjNjYwMWMifSwicm9sZSI6InVzZXIiLCJpYXQiOjE2NTc1MTEwOTh9.IGDgw8MNIrSlYPsJYKPAfQ8BtfVrbTfxq3iag3-ZywU")

  val scn: ScenarioBuilder = scenario("Get list of payment links")
    .exec(http("get products")
      .get("/api/v2/product")
      .check(status.is(200)))


  setUp(
    scn.inject(
      nothingFor(1 seconds),
      rampUsers(userCount) during (rampDuration seconds)
    )
  ).protocols(httpProtocol)
    .maxDuration(testDuration seconds)
    .assertions(
      global.responseTime.max.lt(5000),
      global.failedRequests.percent.is(0)
    )
}

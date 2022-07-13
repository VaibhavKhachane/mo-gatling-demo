package simulation

import io.gatling.core.Predef.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import javafx.util.Duration.seconds

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
class LoadTest extends Simulation{

  val httpConf=http.baseUrl("https://mobile-store-gateway-dev.xendit.co") // without proxy setting
  //val httpConf=http.proxy(Proxy("localhost",8888))
  .baseUrl("https://mobile-store-gateway-dev.xendit.co") // for proxy setting
    .header("Content-Type", "application/json")
    .header("x-session-token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXNzaW9uX3V1aWQiOiIzMzA1NDAxOC1iZTJmLTQ4ODUtYTA3MC1mODZjN2E2NWY2ZTMiLCJ1c2VyIjp7ImlkIjoiNjJjYjk5OWRlZjJiMjYwMDIwYzY2MDFiIn0sImJ1c2luZXNzIjp7ImlkIjoiNjJjYjk5OWFlMmZmMjg3NjA4ZTJkMjJmIn0sInVzZXJfYnVzaW5lc3MiOnsiaWQiOiI2MmNiOTk5ZWVmMmIyNjAwMjBjNjYwMWMifSwicm9sZSI6InVzZXIiLCJpYXQiOjE2NTc1MzQ4MDR9.eTSl0jW91haAMlkscuAtspCp-JP2IIP1O91gu4KnNXE")

  def login() = {

      exec(http("Login")
        .post("/api/auth/email")
        .body(RawFileBody("/Users/apple/Desktop/Xendit/Framework/mobile-store/src/test/resources/bodies/login.json")).asJson
        .check(status.in(200,204)))
  }

  val scn = scenario("mobile store scenarios")
    .exec(login())

  setUp(
    scn.inject(
      nothingFor(2), // nothing do anything for 2 sec
      atOnceUsers(5), // send 5 req
      rampUsers(200) during(20 seconds) // send 50 req in next 10 sec
    ).protocols(httpConf))
}

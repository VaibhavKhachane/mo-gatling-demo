package simulation

import io.gatling.core.Predef.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
class LoopRequest extends Simulation {

  val httpConf=http.baseUrl("https://mobile-store-gateway-dev.xendit.co")
    .header("Content-Type", "application/json")
    .header("x-session-token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXNzaW9uX3V1aWQiOiIzMzA1NDAxOC1iZTJmLTQ4ODUtYTA3MC1mODZjN2E2NWY2ZTMiLCJ1c2VyIjp7ImlkIjoiNjJjYjk5OWRlZjJiMjYwMDIwYzY2MDFiIn0sImJ1c2luZXNzIjp7ImlkIjoiNjJjYjk5OWFlMmZmMjg3NjA4ZTJkMjJmIn0sInVzZXJfYnVzaW5lc3MiOnsiaWQiOiI2MmNiOTk5ZWVmMmIyNjAwMjBjNjYwMWMifSwicm9sZSI6InVzZXIiLCJpYXQiOjE2NTc1MzQ4MDR9.eTSl0jW91haAMlkscuAtspCp-JP2IIP1O91gu4KnNXE")

  def getBalance() = {
    repeat(2) {
      exec(http("GET Balance")
        .get("/api/home/balance")
        .check(status.in(200,304)))
    }
  }

  def getListofProducts() = {
    repeat(2) {
      exec(http("GET List of Products")
        .get("/api/v2/product")
        .check(status.in(200,304)))
    }
  }

  def addProduct() = {
    repeat(2) {
      exec(http("Add Products")
        .post("/api/v2/product")
        .body(RawFileBody("/Users/apple/Desktop/Xendit/Framework/mobile-store/src/test/resources/bodies/products.json")).asJson
        .check(status.in(200,204)))
    }
  }

  val scn = scenario("mobile store scenarios")
    .exec(getBalance())
    .pause(2)
    .exec(getListofProducts())
    .pause(2)
    .exec(addProduct())

  setUp(
    scn.inject(atOnceUsers(1))).protocols(httpConf)

}



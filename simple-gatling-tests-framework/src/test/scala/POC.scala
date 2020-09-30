package src.test.scala


import java.util.UUID
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import javafx.util.Duration.seconds


class POC extends Simulation{

  val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ2cC15d3hGanhUUUNZUUVSVjktQl9hdV9WZGlJUnRJbmMtQ1lBajU2STd3In0.eyJleHAiOjE2MDA2MjU3NTIsImlhdCI6MTU5ODAzMzc1MiwianRpIjoiNWFkZDVjY2QtMmM5YS00NTg0LWI3YzUtMDkyOGI1YTE2MTUxIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLWh0dHAuYXV0aGVudGljYXRpb24uc3ZjLkdUQi5sb2NhbC9hdXRoL3JlYWxtcy9iaXpuZXh0IiwiYXVkIjpbImJpem5leHQtYWRtaW4iLCJiaXpuZXh0Il0sInN1YiI6IjBjMWQ1OTY2LTIwNzktNDg2ZC04MjdiLTdhMjhmNWE5NDMwNyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImptZXRlciIsInNlc3Npb25fc3RhdGUiOiJiODYxZGIwMS0zNDFmLTQ2MzItYmU3YS04YTc2MTk1OTM0NmYiLCJhY3IiOiIxIiwic2NvcGUiOiJwcm9maWxlIiwiY2xpZW50SG9zdCI6IjEwMC4xMjcuMjEyLjI0OCIsImNsaWVudElkIjoiam1ldGVyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWptZXRlciIsImNsaWVudEFkZHJlc3MiOiIxMDAuMTI3LjIxMi4yNDgifQ.dITgBmJB68enbQnFs4AaoP4kZtwjZHrt_NihPcE6dH5HqXjs_Pl6GlnL1ubXXXlo1VztU6osfYO3J0iHMLJ690dgZUptWALVTlNFXT2NWmTMAl5xT5A5Ukf4Eq4B0hD0qSCZu1nEngqSMZe3XC4dPFg0k4cowZe6OVWKssMzaM4P2KBxAMnTTGWL4BKMXoA1G2A1dDibHPtu0UGzO9EppAG82rWliHjQcJEFBGG5mLGhPkCoygLSptgyy6m2X8q0XqM5mz6ZQ6iHt0U3sBpyxeNJ5YMYqOrykLwmi8VoYMTqU1vMmTvk6t7ZtW8YDNmFvF3Ym1zheeALqvzduo8v5A"

  val uuidFeeder  = Iterator.continually(Map("uniqueId" -> UUID.randomUUID().toString))

  val headers = Map("Content-Type" -> "application/json", "Accept" -> "application/json, text/plain, */*","x-original-source-system" ->"ABCSYS","x-request-by"->"10926")

  val httpConf = http
    .baseUrl("http://100.127.212.225").disableCaching.disableWarmUp
    .headers(headers).userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36").authorizationHeader(s"Bearer ${token}").header("x-request-by","10926").header("x-request-id", "${uniqueId}")


  val scn1 = scenario("No Operation").feed(uuidFeeder)
    .exec(
      http("No Operation Request")
        .get("/ktb/rest/poc/v1/hello-world/noop") // Local
        .headers(headers)).pause(5)

  val scn2 = scenario("Request To Database").feed(uuidFeeder)
    .exec(
      http("Request To Database Request")
        .get("/ktb/rest/redhat/v1/hello-world/db-inquiry") // Local
        .headers(headers)).pause(5)

  val scn3 = scenario("Request To CBS").feed(uuidFeeder)
    .exec(http("Request To CBS Request")
      .post("/ktb/rest/redhat/v1/hello-world/cbs-call").body(StringBody("""{"accountNO": 6700181844}""")).asJson).pause(5)


  setUp(scn1.inject(rampConcurrentUsers(0) to (6) during(60),constantConcurrentUsers(6) during(60)), scn2.inject(rampConcurrentUsers(0) to (6) during(10),constantConcurrentUsers(6) during(60)),scn3.inject(rampConcurrentUsers(0) to (6) during(10),constantConcurrentUsers(6) during(60))).protocols(httpConf)


  //setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)





}

package com.lamoroso.example.http.routes

import zio.ZIO
import com.lamoroso.example.services.health.HealthCheck
import zio.UIO
import com.lamoroso.example.services.Health.Healthy
import com.lamoroso.example.services.Health.Unhealthy
import com.lamoroso.example.services.Health.ShuttingDown
import zio.IO
import cats.implicits._

import org.http4s.HttpRoutes
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import zio.URIO
import zio.Task
import zio._
import zio.interop.catz._
import zio.logging.Logging


object HealthCheckRoutes {
  private val healthCheck: ZIO[HealthCheck with Logging, String, String] =
    HealthCheck.healthStatus.orElseFail("Internal failure.").flatMap {
      case Healthy      => UIO.succeed("Healthy!")
      case Unhealthy    => IO.fail("Unhealthy")
      case ShuttingDown => IO.fail("Shutting Down")
    }

  private val aliveEndpoint: ZEndpoint[Unit, String, String] =
    endpoint.get.in("health").errorOut(stringBody).out(jsonBody[String])

  private val aliveRoute: URIO[HealthCheck with Logging, HttpRoutes[Task]] =
    aliveEndpoint.toRoutesR(_ => healthCheck)


  val routes: URIO[HealthCheck with Logging, HttpRoutes[Task]] = for {
    aliveRoute <- aliveRoute
  } yield aliveRoute 
}

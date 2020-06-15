package com.lamoroso.example.http

import zio.URIO
import com.lamoroso.example.services.health.HealthCheck
import com.lamoroso.example.http.routes.HealthCheckRoutes
import org.http4s.HttpApp
import zio.Task
import zio.interop.catz._
import zio.interop.catz.implicits._

import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import com.lamoroso.example.config._
import com.lamoroso.example.AppEnv

import zio.clock.Clock
import zio._
import zio.logging.Logging
import com.lamoroso.example.http.routes.UsersRoutes
import com.lamoroso.example.`package`.UserServiceEnv

import cats.implicits._

object Server {
  private val appRoutes: URIO[UserServiceEnv with HealthCheck with Logging, HttpApp[Task]] =
    for {
      userRoutes        <- UsersRoutes.routes
      healthCheckRoutes <- HealthCheckRoutes.routes
    } yield (userRoutes <+> healthCheckRoutes).orNotFound

  val runServer: ZIO[AppEnv, Throwable, Unit] = {

    for {
      app                          <- appRoutes
      svConfig                     <- Config.httpServerConfig
      implicit0(r: Runtime[Clock]) <- ZIO.runtime[Clock]
      bec                          <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC))
      _                            <- BlazeServerBuilder[Task](bec).bindHttp(svConfig.port.value, svConfig.host.value)
                                        .withNio2(true)
                                        .withHttpApp(app)
                                        .serve
                                        .compile
                                        .drain
    } yield ()
  }

}

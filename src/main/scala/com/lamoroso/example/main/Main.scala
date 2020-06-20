package com.lamoroso.example.main

import com.lamoroso.example.logging.Logger
import com.lamoroso.example.services.health.HealthCheck
import com.lamoroso.example.http.Server
import com.lamoroso.example.config._

import zio.logging.log
import zio.{ App, ExitCode, ZEnv, ZIO }
import com.lamoroso.example.services.user.UserService
import zio.blocking.Blocking
import com.lamoroso.example.transactor.DBTransactor
import com.lamoroso.example.persistence.UserPersistenceSQL

object Main extends App {

  val logger               = Logger.live
  val configLayer          = logger >>> Config.live
  val transactorLayer      = Logger.live ++ Blocking.live ++ configLayer >>> DBTransactor.live
  val userPersistenceLayer = transactorLayer >>> UserPersistenceSQL.live
  val appLayers            = Logger.live ++ configLayer ++ HealthCheck.live ++ UserService.live ++ userPersistenceLayer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    Server.runServer
      .tapError(err => log.error(s"Execution failed with: $err"))
      .provideCustomLayer(appLayers)
      .exitCode

}

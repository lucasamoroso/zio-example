package com.lamoroso.example.main

import zio.App
import zio._
import zio.console._
import com.lamoroso.example.logging.Logger
import com.lamoroso.example.services.health.HealthCheck
import com.lamoroso.example.http.Server
import com.lamoroso.example.config._

import zio.blocking.Blocking
import zio.logging.log
import zio.{ App, ExitCode, ZEnv, ZIO }
object Main extends App {

  val logger = Logger.live
  val config = logger >>> Config.live

  val appLayers  = logger ++ config ++ HealthCheck.live
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] ={
  Server.runServer
      .tapError(err => log.error(s"Execution failed with: $err"))
      .provideCustomLayer(appLayers)
      .exitCode
  }
  

}
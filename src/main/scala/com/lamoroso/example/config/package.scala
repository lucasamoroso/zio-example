package com.lamoroso.example

import scala.jdk.CollectionConverters._

import eu.timepit.refined.pureconfig._
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import zio.logging.{ log, Logging }
import zio.{ Task, _ }

package object config {
  type Config = Has[HttpServerConfig]

  object Config {

    private val basePath = "zio-example"
    private val source   = ConfigSource.default.at(basePath)

    private val buildEnv: Task[String] =
      Task.effect {
        System
          .getenv()
          .asScala
          .map(v => s"${v._1} = ${v._2}")
          .mkString("\n", "\n", "")
      }

    private def logEnv(ex: Throwable): ZIO[Logging, Throwable, Unit] =
      for {
        env <- buildEnv
        _   <- log.error(s"Loading configuration failed with the following environment variables: $env.")
        _   <- log.error(s"Error thrown was $ex.")
      } yield ()

    val live: ZLayer[Logging, Throwable, Config] = ZLayer.fromEffectMany(
      Task
        .effect(source.loadOrThrow[Configuration])
        .map(c => Has(c.httpServer))
        .tapError(logEnv)
    )

    val httpServerConfig: URIO[Has[HttpServerConfig], HttpServerConfig] = ZIO.service

  }
}

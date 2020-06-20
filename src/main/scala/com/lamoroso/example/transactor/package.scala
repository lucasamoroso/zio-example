package com.lamoroso.example

import zio.Has
import doobie.util.transactor.Transactor
import zio.Task
import com.lamoroso.example.config.DbConfig
import scala.concurrent.ExecutionContext
import doobie.hikari.HikariTransactor
import zio.ZManaged
import zio.blocking.Blocking
import zio.ZIO
import com.lamoroso.example.config.Config
import zio.Managed
import zio.blocking
import zio.interop.catz._
import cats.effect.Blocker
import zio.URIO
import zio.ZLayer
import zio.logging.Logging

package object transactor {
  type DBTransactor = Has[Transactor[Task]]

  object DBTransactor {
    private def makeTransactor(
      conf: DbConfig,
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
    ): Managed[Throwable, Transactor[Task]] =
      HikariTransactor
        .newHikariTransactor[Task](
          conf.className,
          conf.url,
          conf.user,
          conf.password,
          connectEC,
          Blocker.liftExecutionContext(transactEC)
        )
        .toManagedZIO

    val managed: ZManaged[Has[DbConfig] with Blocking, Throwable, Transactor[Task]] =
      for {
        config     <- Config.dbConfig.toManaged_
        connectEC  <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blockingEC <- blocking.blocking(ZIO.descriptor.map(_.executor.asEC)).toManaged_
        transactor <- makeTransactor(config, connectEC, blockingEC)
      } yield transactor

    val live: ZLayer[Has[DbConfig] with Logging with Blocking, Throwable, DBTransactor] =
      ZLayer.fromManaged(managed)

    val transactor: URIO[DBTransactor, Transactor[Task]] = ZIO.service

  }

}

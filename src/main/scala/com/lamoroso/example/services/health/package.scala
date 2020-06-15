package com.lamoroso.example.services

import zio.RIO
import zio.Has
import zio.{ ULayer, ZLayer }
import zio.Task
import com.lamoroso.example.services.health._
import zio.ZIO
import zio.logging._

package object health {

  type HealthCheck = Has[HealthCheck.Service]

  object HealthCheck {
    trait Service {
      def healthStatus(): ZIO[Logging, Throwable, Health]
    }

    val live: ULayer[HealthCheck] = ZLayer.succeed({
      new Service {
        override def healthStatus(): ZIO[Logging, Throwable, Health] =
          for {
            _      <- log.info("Checking if healty ...")
            status <- Task(Health.Healthy)
          } yield (status)
      }
    })

    //Accessors
    val healthStatus: ZIO[HealthCheck with Logging, Throwable, Health] = RIO.accessM(_.get.healthStatus())
  }
}

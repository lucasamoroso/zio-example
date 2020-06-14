package com.lamoroso.example.services

import zio.RIO
import zio.Has
import zio.{ ULayer, ZLayer }
import zio.Task
import com.lamoroso.example.services.health._
import zio.ZIO

package object health {

  type HealthCheck = Has[HealthCheck.Service]

  object HealthCheck {
    trait Service {
      def healthStatus(): Task[Health]
    }

    val live: ULayer[HealthCheck] = ZLayer.succeed({
      new Service {
        override def healthStatus(): Task[Health] = Task(Health.Healthy)
      }
    })

    //Accessors
    val healthStatus: RIO[HealthCheck, Health] = RIO.accessM(_.get.healthStatus())
  }
}

package com.lamoroso.example.services

import zio.RIO
import zio.ZLayer
import zio.ULayer
import zio.Has
import zio.logging.Logging
import zio.logging.log
import zio.ZIO
import com.lamoroso.example.model.User

package object user {
  type UserService = Has[UserService.Service]

  object UserService {
    trait Service {
      def createUser(user: User): RIO[Logging, Unit]
    }

    val live: ULayer[UserService] = ZLayer.succeed {
      new UserService.Service {
        override def createUser(user: User): RIO[Logging, Unit] =
          for {
            _ <- log.info("Creating new user")
          } yield ()
      }
    }

    //accesors
    def createUser(user: User): RIO[UserService with Logging, Unit] =
      ZIO.accessM(_.get.createUser(user))

  }
}

package com.lamoroso.example.services

import zio.RIO
import zio.ZLayer
import zio.ULayer
import zio.Has
import zio.logging.Logging
import zio.logging.log
import zio.ZIO
import com.lamoroso.example.model.User
import com.lamoroso.example.persistence.UserPersistence
import com.lamoroso.example.persistence.errors.DatabaseError

package object user {
  type UserService = Has[UserService.Service]

  object UserService {
    trait Service {
      def createUser(user: User): RIO[UserPersistence with Logging, Unit]
    }

    val live: ULayer[UserService] = ZLayer.succeed {
      new UserService.Service {
        override def createUser(user: User): RIO[UserPersistence with Logging, Unit] =
          for {
            _ <- UserPersistence.create(user).mapError { err =>
                   log.error(s"Unexpected database error ${err.getMessage}")
                   DatabaseError(err.getMessage)
                 }
          } yield ()
      }
    }

    //accesors
    def createUser(user: User): RIO[UserService with UserPersistence with Logging, Unit] =
      ZIO.accessM(_.get.createUser(user))

  }
}

package com.lamoroso.example

import com.lamoroso.example.model.User
import zio.Task
import zio.Has
import zio.RIO

package object persistence {
  type UserPersistence = Has[UserPersistence.Service]

  object UserPersistence {
    trait Service {
      def create(user: User): Task[User]
    }
    def create(user: User): RIO[UserPersistence, User] = RIO.accessM(_.get.create(user))
  }

}

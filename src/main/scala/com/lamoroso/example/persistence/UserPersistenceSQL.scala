package com.lamoroso.example.persistence

import zio.Task
import doobie.util.transactor.Transactor
import com.lamoroso.example.model.User
import doobie.util.update.Update0
import zio.ZLayer
import com.lamoroso.example.transactor.DBTransactor
import com.lamoroso.example.persistence.UserPersistenceSQL.Queries
import com.lamoroso.example.persistence.errors._

import doobie.implicits._
import zio.interop.catz._

class UserPersistenceSQL(tx: Transactor[Task]) extends UserPersistence.Service {

  override def create(user: User): zio.Task[User] =
    Queries
      .create(user)
      .run
      .transact(tx)
      .bimap(err => DatabaseError(err.getMessage), _ => user)

}

object UserPersistenceSQL {
  def apply(tx: Transactor[Task]): UserPersistenceSQL = UserPersistenceSQL(tx)

  object Queries {
    def create(user: User): Update0 =
      sql"""INSERT INTO USERS (name, email) VALUES (${user.name}, ${user.email})""".update

  }

  val live: ZLayer[DBTransactor, Throwable, UserPersistence] =
    ZLayer.fromEffect(
      DBTransactor.transactor.map(new UserPersistenceSQL(_))
    )
}

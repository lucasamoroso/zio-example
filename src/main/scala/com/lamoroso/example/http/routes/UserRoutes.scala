package com.lamoroso.example.http.routes

import sttp.tapir.ztapir.{ ZEndpoint, endpoint, statusMapping, _ }

import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import zio.interop.catz._
import com.lamoroso.example.model.User
import com.lamoroso.example.http.ClientError._
import com.lamoroso.example.http.ClientError
import com.lamoroso.example.services.user.UserService
import zio.URIO
import zio.Task
import com.lamoroso.example.`package`._

object UsersRoutes {
  val httpErrors = oneOf(
    statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
    statusMapping(StatusCode.BadRequest, jsonBody[BadRequest]),
    statusMapping(StatusCode.NotFound, jsonBody[NotFound])
  )

  private val createUserEndpoint: ZEndpoint[User, ClientError, Unit] =
    endpoint.post
      .in("users")
      .in(jsonBody[User])
      .errorOut(httpErrors)

  private val createUserRoute: URIO[UserServiceEnv, HttpRoutes[Task]] =
    createUserEndpoint.toRoutesR { user =>
      UserService
        .createUser(user)
        .mapToClientError
    }

  val routes: URIO[UserServiceEnv, HttpRoutes[Task]] = for {
    createUserRoute <- createUserRoute
  } yield createUserRoute

}

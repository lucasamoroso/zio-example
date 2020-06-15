package com.lamoroso.example.http

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.ZIO
import zio.logging._
import zio.Cause


sealed trait ClientError extends Product with Serializable

object ClientError {
  final case class InternalServerError(message: String) extends ClientError
  object InternalServerError {
    implicit val codec: Codec[InternalServerError] = deriveCodec
  }

  final case class BadRequest(message: String) extends ClientError
  object BadRequest {
    implicit val codec: Codec[BadRequest] = deriveCodec
  }

  final case class NotFound(message: String) extends ClientError
  object NotFound {
    implicit val codec: Codec[NotFound] = deriveCodec
  }

  /**
    * Expose the full error cause when handling a request and recovers failures and deaths appropriately.
    */
  implicit class mapError[R, E, A](private val f: ZIO[R, E, A]) extends AnyVal {
    def mapToClientError: ZIO[R with Logging, ClientError, A] =
      f.tapError(error => log.error(s"Error processing request: $error."))
        .mapErrorCause { cause =>
          val clientFailure = cause.failureOrCause match {
            case _                          => InternalServerError("Internal Server Error")
          }
          Cause.fail(clientFailure)
        }
  }
}

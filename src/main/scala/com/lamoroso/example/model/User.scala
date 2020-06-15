package com.lamoroso.example.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec


final case class User(name: String, email: String)

object User {
  implicit val codec: Codec[User] = deriveCodec
}
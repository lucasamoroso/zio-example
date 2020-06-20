package com.lamoroso.example.persistence.errors

final case class DatabaseError(msg: String) extends Throwable

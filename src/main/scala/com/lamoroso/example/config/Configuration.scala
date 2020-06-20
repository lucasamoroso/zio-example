package com.lamoroso.example.config

final case class Configuration(
  httpServer: HttpServerConfig,
  db: DbConfig
)

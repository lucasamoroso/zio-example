package com.lamoroso
import com.lamoroso.example.config._
import com.lamoroso.example.services.health.HealthCheck


import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import zio._
import zio.clock.Clock
import zio.logging.Logging
import zio.stream.Stream

package object example {
  type AppEnv = ZEnv with Config with HealthCheck with Logging with Clock
}

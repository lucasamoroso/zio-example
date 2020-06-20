package com.lamoroso
import com.lamoroso.example.config._
import com.lamoroso.example.services.health.HealthCheck

import zio._
import zio.clock.Clock
import zio.logging.Logging
import com.lamoroso.example.services.user.UserService

package object example {
  type UserServiceEnv = UserService with Logging
  type AppEnv         = ZEnv with UserServiceEnv with Config with HealthCheck with Logging with Clock
}

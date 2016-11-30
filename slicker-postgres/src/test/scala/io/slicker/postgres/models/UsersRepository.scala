package io.slicker.postgres.models

import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.PostgreSQLRepository

import scala.concurrent.ExecutionContext

class UsersRepository extends PostgreSQLRepository(new UserTable){

  override protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

}

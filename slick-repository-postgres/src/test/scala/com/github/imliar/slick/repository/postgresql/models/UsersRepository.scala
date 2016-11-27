package com.github.imliar.slick.repository.postgresql.models

import com.github.imliar.slick.repository.postgresql.PostgreSQLRepository

import com.github.imliar.slick.repository.postgresql.PostgresDriver.api._

import scala.concurrent.ExecutionContext

class UsersRepository extends PostgreSQLRepository(new UserTable){

  override protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

}

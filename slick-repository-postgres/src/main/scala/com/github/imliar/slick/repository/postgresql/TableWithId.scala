package com.github.imliar.slick.repository.postgresql

import slick.driver.PostgresDriver.api._

abstract class TableWithId[Id, R](tag: Tag, tableName: String) extends Table[R](tag, tableName) {
  def id: Rep[Id]
}
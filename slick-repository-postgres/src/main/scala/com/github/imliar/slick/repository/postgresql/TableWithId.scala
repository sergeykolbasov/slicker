package com.github.imliar.slick.repository.postgresql

import PostgresDriver.api._

abstract class TableWithId[Id, R](tag: Tag, tableName: String) extends Table[R](tag, tableName) {
  def id: Rep[Id]
}
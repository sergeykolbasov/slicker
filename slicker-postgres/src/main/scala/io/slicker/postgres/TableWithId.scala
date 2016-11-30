package io.slicker.postgres

import io.slicker.postgres.PostgresDriver.api._

abstract class TableWithId[Id, R](tag: Tag, tableName: String) extends Table[R](tag, tableName) {
  def id: Rep[Id]
}
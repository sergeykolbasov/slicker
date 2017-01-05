package io.slicker.postgres.models

import io.slicker.core.sort.Sort
import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.{SimpleRecordTable, TableWithId}
import slick.lifted.ProvenShape

import scala.language.experimental.macros

class UserTable extends SimpleRecordTable[Long, User, Users] {

  override val tableQuery: TableQuery[Users] = TableQuery[Users]

  override def order: Sort[Users] = Sort.auto[Users]

}

class Users(tag: Tag) extends TableWithId[Long, User](tag, "users") {

  override def id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  override def * : ProvenShape[User] = (id.?, name) <> (User.tupled, User.unapply)

}
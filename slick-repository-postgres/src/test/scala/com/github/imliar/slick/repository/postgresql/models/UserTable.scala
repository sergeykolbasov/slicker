package com.github.imliar.slick.repository.postgresql.models

import com.github.imliar.slick.repository.postgresql.{RecordTable, TableWithId}
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

class UserTable extends RecordTable[Long, User, User, Users] {

  override val tableQuery: TableQuery[Users] = TableQuery[Users]

  /**
    * Conversion from business entity to database
    */
  override def toDatabase(business: User): User = business

  /**
    * Conversion from database entity to business
    */
  override def toBusiness(database: User): User = database
}

class Users(tag: Tag) extends TableWithId[Long, User](tag, "users") {

  override def id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  override def * : ProvenShape[User] = (id.?, name) <> (User.tupled, User.unapply)

}
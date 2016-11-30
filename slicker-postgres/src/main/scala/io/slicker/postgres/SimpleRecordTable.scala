package io.slicker.postgres

import io.slicker.postgres.PostgresDriver.api._

/**
  * Basic trait for records entities
  *
  * @tparam Id Type of ID parameter of entity
  * @tparam Business Type of entity
  * @tparam T Type of slick table
  */
trait SimpleRecordTable[Id, Business, T <: TableWithId[Id, Business]] extends RecordTable[Id, Business, Business, T] {

  /**
    * Instance of slick table query
    */
  val tableQuery: TableQuery[T]

  /**
    * Conversion from business entity to database
    */
  def toDatabase(business: Business): Business = business

  /**
    * Conversion from database entity to business
    */
  def toBusiness(database: Business): Business = database

}

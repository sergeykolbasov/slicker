package io.slicker.postgres

import io.slicker.core.sort.Sort
import io.slicker.postgres.PostgresDriver.api._

/**
  * Basic trait for records entities
  *
  * @tparam Id Type of ID parameter of entity
  * @tparam Business Type of business entity
  * @tparam Database Type of record entity
  * @tparam T Type of slick table
  */
trait RecordTable[Id, Business, Database, T <: TableWithId[Id, Database]] {

  /**
    * Instance of slick table query
    */
  val tableQuery: TableQuery[T]

  /**
    * Conversion from business entity to database
    */
  def toDatabase(business: Business): Database

  /**
    * Conversion from database entity to business
    */
  def toBusiness(database: Database): Business

  /**
    * [[Sort]] object that will return [[slick.lifted.Ordered]]
    * for some input string as field name to sort by
    */
  def sort: Sort[T] = Sort.empty[T]

}

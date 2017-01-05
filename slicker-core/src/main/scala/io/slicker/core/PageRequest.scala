package io.slicker.core

/**
  * Page request that allows to set limit/offset values for a request in terms of pagination
  *
  * @param page    Page number. Should always be equal or greater than 1
  * @param perPage Number of entities to get from request. Should always be equal or greater than 0
  * @param sort    Fields to sort by
  */
case class PageRequest(page: Int, perPage: Int, sort: Fields = Fields.empty) {

  /**
    * Offset value for SQL queries
    */
  def offset: Int = (page - 1) * perPage

  /**
    * Limit value for SQL queries
    */
  def limit: Int = perPage

}

object PageRequest {

  /**
    * Requesting all pages
    */
  val ALL = new PageRequest(1, Int.MaxValue)

  /**
    * Requesting first page
    */
  val FIRSTPAGE = new PageRequest(1, 10)

  def apply(sort: Fields): PageRequest = PageRequest(1, Int.MaxValue, sort)

}

case class Fields(fields: Seq[(String, SortDirection)])

object Fields {
  def empty: Fields = Fields(Seq.empty)
}

sealed abstract class SortDirection(val name: String) {

  def isAsc: Boolean = this match {
    case SortDirection.Asc => true
    case SortDirection.Desc => false
  }

  def isDesc: Boolean = !isAsc

}

object SortDirection {

  case object Asc extends SortDirection("asc")

  case object Desc extends SortDirection("desc")

  def apply(name: String): SortDirection = name match {
    case Asc.name => Asc
    case Desc.name => Desc
  }

}
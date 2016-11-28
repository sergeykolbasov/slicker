package io.slicker.core

/**
  * Page request that allows to set limit/offset values for a request in terms of pagination
  * @param page Page number. Should always be equal or greater than 1
  * @param perPage Number of entities to get from request. Should always be equal or greater than 0
  */
case class PageRequest(page: Int , perPage: Int) {

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

}
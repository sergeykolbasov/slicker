package io.slicker.core

import slick.dbio._

/**
  * Public interface for each repository implementation
  *
  * @tparam Id Type of ID parameter
  * @tparam E  Type of entity class
  */
trait Repository[Id, E] {

  type WriteAction[A] = DBIOAction[A, NoStream, Effect.Write]

  type ReadAction[A] = DBIOAction[A, NoStream, Effect.Read]

  /**
    * Insert or updates entity in DB
    *
    * @param e entity
    * @return inserted entity with new ID or updated entity
    */
  def save(e: E): WriteAction[E]

  /**
    * Insert or updates entities in DB
    *
    * @param es entities
    * @return inserted or updated entities
    */
  def save(es: Seq[E]): WriteAction[Seq[E]]

  /**
    * Find all entities
    *
    * @param pageRequest request could be limited with offset/limit
    * @return All entities in table in respect with pageRequest parameter
    */
  def findAll(pageRequest: PageRequest = PageRequest.ALL): ReadAction[Seq[E]]

  /**
    * Find entity by id
    *
    * @param id
    * @return Option with entity. None in case if there is no such entity.
    */
  def findById(id: Id): ReadAction[Option[E]]

  /**
    * Remove entity by id
    *
    * @param id
    * @return True in case if entity was removed
    */
  def removeById(id: Id): WriteAction[Boolean]

  /**
    * Remove entity
    *
    * @param e
    * @return True in case if entity was removed
    */
  def remove(e: E): WriteAction[Boolean]

  /**
    * Remove entities
    *
    * @param es
    * @return True in case if at least one of entities was removed
    */
  def remove(es: Seq[E]): WriteAction[Boolean]

  /**
    * Remove all entities in table
    * @return True in case if at least one of entities was removed
    */
  def removeAll(): WriteAction[Boolean]

  /**
    * Count all rows in table
    *
    * @return Number of rows
    */
  def countAll: ReadAction[Int]

}

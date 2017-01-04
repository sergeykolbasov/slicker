package io.slicker.postgres

import io.slicker.core._
import io.slicker.postgres.PostgresDriver.api._
import slick.ast.BaseTypedType
import slick.dbio.DBIOAction
import slick.lifted.CanBeQueryCondition

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

abstract class PostgreSQLRepository[Id : BaseTypedType, E, R, T <: TableWithId[Id, R]](protected val table: RecordTable[Id, E, R, T])(implicit entity: Entity[E, Id])
  extends Repository[Id, E] {

  protected implicit val ec: ExecutionContext

  protected val tableQuery: TableQuery[T] = table.tableQuery

  protected def id(e: E): Option[Id] = implicitly[Entity[E, Id]].id(e)

  /**
    * Insert or updates entity in DB
    *
    * @param e entity
    * @return inserted entity with new ID or updated entity
    */
  def save(e: E): WriteAction[E] = {
    id(e) match {
      case Some(id) =>
        tableQuery
          .filter(_.id === id)
          .update(table.toDatabase(e))
          .map(_ => e)
      case None =>
        tableQuery
          .returning(tableQuery)
          .into((_, res) => res)
          .+=(table.toDatabase(e))
          .map(table.toBusiness)
    }
  }

  /**
    * Insert or updates entities in DB
    *
    * @param es entities
    * @return inserted or updated entities
    */
  def save(es: Seq[E]): WriteAction[Seq[E]] = {
    DBIOAction.sequence(es.map(save))
  }

  /**
    * Find all entities
    *
    * @param pageRequest request could be limited with offset/limit
    * @return All entities in table in respect with pageRequest parameter
    */
  def findAll(pageRequest: PageRequest = PageRequest.ALL): ReadAction[Seq[E]] = {
    tableQuery
      .map(identity)
      .withPageRequest(pageRequest)
      .result
      .map(_.map(table.toBusiness))
  }

  /**
    * Find entity by id
    *
    * @param id
    * @return Option with entity. None in case if there is no such entity.
    */
  def findById(id: Id): ReadAction[Option[E]] = {
    findOneBy(_.id === id)
  }

  /**
    * Remove entity by id
    *
    * @param id
    * @return True in case if entity was removed
    */
  def removeById(id: Id): WriteAction[Boolean] = {
    removeBy(_.id === id)
  }

  /**
    * Remove entity
    *
    * @param e
    * @return True in case if entity was removed
    */
  def remove(e: E): WriteAction[Boolean] = {
    removeById(id(e).getOrElse(throw new IllegalStateException("Entity is required to have an id for removal")))
  }

  /**
    * Remove entities
    *
    * @param es
    * @return True in case if at least one of entities was removed
    */
  def remove(es: Seq[E]): WriteAction[Boolean] = {
    DBIOAction.sequence(es.map(remove)).map(_ => true)
  }

  /**
    * Remove all entities in table
 *
    * @return True in case if at least one of entities was removed
    */
  def removeAll(): WriteAction[Boolean] = {
    tableQuery.delete.map(_ > 0)
  }

  /**
    * Count all rows in table
    *
    * @return Number of rows
    */
  def countAll: ReadAction[Int] = {
    tableQuery.length.result
  }

  /**
    * Remove rows from table by predicate.
    * {{
    * removeBy(_.foo === "bar")
    * }}
    *
    * @param f                   predicate
    * @param canBeQueryCondition Proof that result of predicate could be a query condition
    * @tparam P Type of predicate. it's not a boolean but slick internal
    * @return true in case if one more rows were removed
    */
  protected def removeBy[P <: Rep[_]](f: T => P)(implicit canBeQueryCondition: CanBeQueryCondition[P]): WriteAction[Boolean] = {
    tableQuery.filter(f).delete.map(_ > 0)
  }

  /**
    * Count rows by predicate.
    * {{
    * countBy(_.foo === "bar")
    * }}
    *
    * @param f                   predicate
    * @param canBeQueryCondition Proof that result of predicate could be a query condition
    * @tparam P Type of predicate. it's not a boolean but slick internal
    * @return Number of rows that satisfied predicate
    */
  protected def countBy[P <: Rep[_]](f: T => P)(implicit canBeQueryCondition: CanBeQueryCondition[P]): ReadAction[Int] = {
    tableQuery.filter(f).length.result
  }

  /**
    * Find one entity by predicate
    *
    * @param f                   predicate
    * @param canBeQueryCondition proof that result of predicate could be a query condition
    * @tparam P type of predicate. it's not a boolean but slick internal
    * @return First entity that satisfied given predicate. If there is no such entity, None will be returned
    */
  protected def findOneBy[P <: Rep[_]](f: T => P)(implicit canBeQueryCondition: CanBeQueryCondition[P]): ReadAction[Option[E]] = {
    tableQuery.filter(f).result.headOption.map(_.map(table.toBusiness))
  }

  /**
    * Find all entities by predicate
    *
    * @param f                   predicate
    * @param pageRequest         page request to limit/offset query
    * @param canBeQueryCondition proof that result of predicate could be a query condition
    * @tparam P type of predicate. it's not a boolean but slick internal
    * @return
    */
  protected def findAllBy[P <: Rep[_]](f: T => P, pageRequest: PageRequest = PageRequest.ALL)
                                      (implicit canBeQueryCondition: CanBeQueryCondition[P]): ReadAction[Seq[E]] = {
    tableQuery
      .filter(f)
      .withPageRequest(pageRequest)
      .result.map(_.map(table.toBusiness))
  }

  /**
    * Helper for using PageRequest along with slick
    */
  protected implicit class QueryWithPageRequest[UQ, CQ[_]](q: Query[T, UQ, CQ]) {
    def withPageRequest(pr: PageRequest): Query[T, UQ, CQ] = {
      val withOffset = if(pr.perPage == Int.MaxValue) {
        q
      } else {
        q.drop(pr.offset).take(pr.perPage)
      }
      withOffset.sortBy(t => table.order(t, pr.sort.fields))
    }
  }

}
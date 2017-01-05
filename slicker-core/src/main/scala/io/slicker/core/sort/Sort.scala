package io.slicker.core.sort

import io.slicker.core.SortDirection
import io.slicker.core.sort.gen.SortMacros
import slick.ast.Ordering
import slick.lifted.Ordered

import scala.language.experimental.macros
import scala.language.implicitConversions

/**
  * For some table `T` provide sorting mechanic
  *
  * @tparam T table
  */
trait Sort[T] {

  /**
    * Build [[slick.lifted.Ordered]] for given table & field with provided direction
    *
    * @param table         table with fields
    * @param field         field to sort by
    * @param sortDirection direction (asc or desc)
    * @return If it's possible to sort by `field` of table `T`, return [[slick.lifted.Ordered]]. In other case return `None`
    */
  protected def ordered(table: T, field: String, sortDirection: io.slicker.core.SortDirection): Option[slick.lifted.Ordered]

  /**
    * Build [[slick.lifted.Ordered]] for given table & fields with provided direction
    *
    * @param table  table with fields
    * @param fields fields to sort by with direction for each field
    * @return Will return non-empty [[slick.lifted.Ordered]] if it's possible to sort at least by one field.
    */
  def apply(table: T, fields: Seq[(String, SortDirection)]): Ordered = {
    val os = fields.flatMap({
      case (field, direction) => ordered(table, field, direction)
    })
    os.foldLeft(new Ordered(columns = IndexedSeq.empty))((p, c) => {
      new Ordered(columns = p.columns ++ c.columns)
    })
  }

}

object Sort {

  /**
    * Manually build [[Sort]] for table `T` using PartialFunction
    *
    * {{{
    *   Sort.manual[Users](userTable => {
    *     case "photo" => userTable.photo
    *     case "email" => userTable.email
    *   })
    * }}}
    *
    * @return [[Sort]] instance that will apply sorting only for names/columns in provided partial function
    */
  def manual[T](pf: T => PartialFunction[String, Ordered]): Sort[T] = {
    new Sort[T] {
      override protected def ordered(table: T, field: String, sortDirection: SortDirection): Option[Ordered] = {
        pf(table).lift(field).map { ordered =>
          new Ordered(columns = ordered.columns.map {
            case (node, _) => (node, sortDirectionToOrdering(sortDirection))
          })
        }
      }
    }
  }

  private def sortDirectionToOrdering(sortDirection: SortDirection): Ordering = {
    sortDirection match {
      case SortDirection.Asc => Ordering().asc
      case SortDirection.Desc => Ordering().desc
    }
  }

  /**
    * Build [[Sort]] for table `T` semiautomatically.
    * It will use provided method names as sorting names. All others fields will be ignored.
    *
    * {{{
    *   Sort.semiauto[Users](table => Seq(table.id, table.name, table.email))
    * }}}
    *
    * @return [[Sort]] instance that will apply sorting only for columns in provided sequence
    */
  def semiauto[T](f: T => Seq[_]): Sort[T] = macro SortMacros.partialImpl[T]

  /**
    * Build [[Sort]] for table `T` automatically
    * It will build [[Sort]] for all columns in table, using method names as sorting names.
    *
    * {{
    *   Sort.auto[Users]
    * }}
    *
    * @return [[Sort]] instance that will apply sorting for all columns in provided table `T`
    */
  def auto[T]: Sort[T] = macro SortMacros.fullImpl[T]

  /**
    * Build empty [[Sort]] for table `T`. Default behaviour for all tables.
    *
    * @return [[Sort]] instance that will always return no fields to sort by
    */
  def empty[T]: Sort[T] = new Sort[T] {
    override protected def ordered(table: T, field: String, sortDirection: SortDirection): Option[Ordered] = None
  }

}
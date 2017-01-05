package io.slicker.core.order

import io.slicker.core.SortDirection
import slick.ast.Ordering
import slick.lifted.{Ordered, Rep}

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.macros.blackbox.Context

trait Order[T] {

  protected def ordered(table: T, field: String, sortDirection: io.slicker.core.SortDirection): Option[slick.lifted.Ordered]

  def apply(table: T, fields: Seq[(String, SortDirection)]): Ordered = {
    val os = fields.flatMap({
      case (field, direction) => ordered(table, field, direction)
    })
    os.foldLeft(new Ordered(columns = IndexedSeq.empty))((p, c) => {
      new Ordered(columns = p.columns ++ c.columns)
    })
  }

}

object Order {

  def fullImpl[T : c.WeakTypeTag](c: Context): c.Expr[Order[T]] = {

    import c.universe._

    val T = weakTypeOf[T]
    val rep = weakTypeOf[Rep[_]]
    val terms = T.decls.filter({
      case m if m.isMethod =>
        val method = m.asMethod
        method.isPublic && !method.isConstructor && method.returnType <:< rep
      case v if !v.isMethod =>
        v.isPublic && v.typeSignature <:< rep
    }).map(_.asTerm).toSeq

    val cases = terms.map({ method =>
      val term = q"table.${method.asTerm.name}"
      val ordered = q"if(sortDirection.isAsc) Some($term.asc) else Some($term.desc)"
      val name = method.name.decodedName.toString
      cq"""$name => $ordered"""
    }) ++ Seq(cq"_ => Option.empty[slick.lifted.Ordered]")

    val names = terms.map(_.name.decodedName.toString).toString()

    c.Expr[io.slicker.core.order.Order[T]] {
      q"""
          new io.slicker.core.order.Order[$T] {
            protected def ordered(table: $T, field: String, sortDirection: io.slicker.core.SortDirection): Option[slick.lifted.Ordered] = {
              field match {
                  case ..$cases
              }
            }
          }
      """
    }

  }

  def partialImpl[T : c.WeakTypeTag](c: Context)(f: c.Expr[T => Seq[_]]): c.Expr[Order[T]] = {

    import c.universe._

    val T = weakTypeOf[T]
    val function = q"$f"
    val arg = function.children.head.asInstanceOf[ValDef]
    val argName = arg.name
    val functionResult = function.children.last.asInstanceOf[Apply]
    val cases = functionResult.args.collect {
      case Select(Ident(prefix), method: TermName) if prefix == argName =>
        val term = q"table.$method"
        val ordered = q"if(sortDirection.isAsc) Some($term.asc) else Some($term.desc)"
        val name = method.decodedName.toString
        cq"$name => $ordered"
    } ++ Seq(cq"_ => Option.empty[slick.lifted.Ordered]")

    c.Expr[Order[T]] {
      q"""
          new io.slicker.core.order.Order[$T] {
            protected def ordered(table: $T, field: String, sortDirection: io.slicker.core.SortDirection): Option[slick.lifted.Ordered] = {
              field match {
                  case ..$cases
              }
            }
          }
      """
    }
  }

  /*def partialImpl[F : c.WeakTypeTag, T : c.WeakTypeTag](c: Context)(f: c.Expr[F]): c.Expr[Order[A]] = {
    import c.universe._

    val F = weakTypeOf[F]
    f.tree
    //F.member(q"apply")
  }*/

  /*def instance[T](pf: T => PartialFunction[String, Ordered]): Order[T] = {
    new Order[T] {
      override protected def ordered(table: T, field: String, sortDirection: SortDirection): Option[Ordered] = {
        pf(table).lift(field).map { ordered =>
          new Ordered(columns = ordered.columns.map {
            case (node, _) => (node, sortDirectionToOrdering(sortDirection))
          })
        }
      }
    }
  }*/
  def instance[T](f: T => Seq[_]): Order[T] = macro partialImpl[T]

  private def sortDirectionToOrdering(sortDirection: SortDirection): Ordering = {
    sortDirection match {
      case SortDirection.Asc => Ordering().asc
      case SortDirection.Desc => Ordering().desc
    }
  }

  def full[T]: Order[T] = macro fullImpl[T]

  def empty[T]: Order[T] = new Order[T] {
    override protected def ordered(table: T, field: String, sortDirection: SortDirection): Option[Ordered] = None
  }

}
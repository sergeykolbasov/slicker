package io.slicker.core.sort.gen

import io.slicker.core.sort.Sort
import slick.lifted.Rep

import scala.reflect.macros.blackbox.Context

object SortMacros {

  /**
    * Build [[io.slicker.core.Fields]] for all fields in table `T`
    */
  def fullImpl[T: c.WeakTypeTag](c: Context): c.Expr[Sort[T]] = {

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

    c.Expr[Sort[T]] {
      q"""
          new io.slicker.core.sort.Sort[$T] {
            protected def ordered(table: $T, field: String, sortDirection: io.slicker.core.SortDirection): Option[slick.lifted.Ordered] = {
              field match {
                  case ..$cases
              }
            }
          }
      """
    }

  }

  /**
    * Build [[Sort]] only for fields in `Seq[_]`
    */
  def partialImpl[T: c.WeakTypeTag](c: Context)(f: c.Expr[T => Seq[_]]): c.Expr[Sort[T]] = {

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

    c.Expr[Sort[T]] {
      q"""
          new io.slicker.core.sort.Sort[$T] {
            protected def ordered(table: $T, field: String, sortDirection: io.slicker.core.SortDirection): Option[slick.lifted.Ordered] = {
              field match {
                  case ..$cases
              }
            }
          }
      """
    }
  }

}

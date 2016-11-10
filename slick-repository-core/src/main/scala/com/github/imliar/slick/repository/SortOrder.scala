package com.github.imliar.slick.repository

sealed abstract class SortOrder(val name: String)

object SortOrder {

  case object Desc extends SortOrder("desc")

  case object Asc extends SortOrder("asc")

  def apply(name: String): SortOrder = name match {
    case Desc.name => Desc
    case Asc.name => Asc
  }
}

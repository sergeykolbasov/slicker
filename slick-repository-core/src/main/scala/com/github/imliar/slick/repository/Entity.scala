package com.github.imliar.slick.repository

import com.github.imliar.slick.repository.generic.EntityGen

/**
  * Basic trait for every entity that should be used with repository
  *
  * @tparam Id Type of ID parameter
  */
trait Entity[E, Id] {
  def id(e: E): Option[Id]
}

object Entity {

  /**
    * Automatically derive [[Entity]] type class if class `E` has `id: Option[A]` field.
    */
  implicit def deriveEntity[E, Id](implicit entityGen: EntityGen.Aux[E, Id]): Entity[E, Id] = entityGen()

}
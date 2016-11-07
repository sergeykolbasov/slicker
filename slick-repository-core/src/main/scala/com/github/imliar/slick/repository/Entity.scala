package com.github.imliar.slick.repository

/**
  * Basic trait for every entity that should be used with repository
  * @tparam Id Type of ID parameter
  */
trait Entity[Id] {
  val id: Option[Id]
}
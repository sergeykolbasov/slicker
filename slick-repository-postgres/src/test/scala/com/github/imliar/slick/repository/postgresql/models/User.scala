package com.github.imliar.slick.repository.postgresql.models

import com.github.imliar.slick.repository.Entity

case class User(id: Option[Long], name: String) extends Entity[Long]

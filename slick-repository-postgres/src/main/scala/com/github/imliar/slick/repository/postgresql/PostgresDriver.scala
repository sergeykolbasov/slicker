package com.github.imliar.slick.repository.postgresql

import com.github.tminglei.slickpg._

class PostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDate2Support
  with PgRangeSupport
  with PgHStoreSupport
  with PgSearchSupport
  with PgNetSupport
  with PgEnumSupport
  with PgLTreeSupport
  with PgCompositeSupport {

  override val api = SlickAPI

  object SlickAPI extends API with ArrayImplicits
    with DateTimeImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

}

object PostgresDriver extends PostgresDriver
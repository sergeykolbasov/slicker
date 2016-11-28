package io.slicker

import io.slicker.postgres.PostgresDriver.api._

import scala.concurrent.Future

package object postgres {

  private val db = Database.forConfig("db")

  implicit class RunDBIO[R, S <: NoStream, E <: Effect](dBIO: DBIOAction[R, S, E]) {

    def run: Future[R] = db.run(dBIO)

  }

  def closeConnection(): Unit = db.close()

}

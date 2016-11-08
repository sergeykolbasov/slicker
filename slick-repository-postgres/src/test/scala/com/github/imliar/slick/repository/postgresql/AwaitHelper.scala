package com.github.imliar.slick.repository.postgresql

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

trait AwaitHelper {

  implicit class AwaitFuture[A](f: Future[A]) {

    def await(implicit timeout: Duration): A = Await.result[A](f, timeout)

  }

}

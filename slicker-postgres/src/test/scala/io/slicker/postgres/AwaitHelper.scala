package io.slicker.postgres

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait AwaitHelper {

  implicit class AwaitFuture[A](f: Future[A]) {

    def await(implicit timeout: Duration): A = Await.result[A](f, timeout)

  }

}

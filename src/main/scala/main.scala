import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object main {

  import helpers.implicitConversion._

  def ret(): Future[String] = {
    (for {
      data <- optionOne or ((err : String) =>  err + " ouaf")
      res <- optionTwo(data) or (_ => " graour")
    } yield res).merge
  }

  def main(args: Array[String]): Unit = {

    val r = ret()
    r.map(el => println(el))

    Await.result(r, 100.seconds)
  }

  def optionOne(): Future[Either[String, String]] = {
    Future { Right("cats say") }
    //Future { Left("cats love")}
  }

  def optionTwo(test: String): Future[Option[String]] = {
    Future {
      Some(test + " miaww")
    }
  }
}


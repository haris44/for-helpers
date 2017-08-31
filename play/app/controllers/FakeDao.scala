package controllers

import javax.inject.Singleton

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object FakeDao  {

  def getOne : Future[Option[String]] =  {
    Future { Some("I'm a teapot") }
  }

  def getTwo : Future[Either[Int, String]] = {
    Future { Right("Hello") }
  }
}

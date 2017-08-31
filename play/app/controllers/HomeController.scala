package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
import helpers.forHelpers._

import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action.async {
    (for {
      one <- FakeDao.getOne |? BadRequest
      two <- FakeDao.getTwo |? InternalServerError
    } yield Ok(one)).merge
  }

}

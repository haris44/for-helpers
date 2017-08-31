package helpers

import cats.data.EitherT
import play.api.libs.json.{JsError, JsResult, JsSuccess}
import play.api.mvc.Result

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object forHelpers {

  type Step[A] = EitherT[Future, ResultType, A]
  type ResultType = Result

  object ActionDSL {

    def fromJsResult[A](onNone: JsError => ResultType)(in: JsResult[A]): Step[A] = EitherT(
      Future.successful(in match {
        case s: JsSuccess[A] => Right(s.get)
        case e: JsError => Left(onNone(e))
      })
    )

    def fromOption[A](onNone: Unit => ResultType)(in: Option[A]): Step[A] = EitherT(
      Future.successful(in.map(el => Right(el)).getOrElse(Left(onNone())))
    )

    def fromFOption[A](onNone: Unit => ResultType)(in: Future[Option[A]]): Step[A] = EitherT(
      in.map((opt: Option[A]) => opt.map(el => Right(el)).getOrElse(Left(onNone())))
    )

    def fromFEither[A, B](onNone: B => ResultType)(in: Future[Either[B, A]]): Step[A] = EitherT(
      in.map((ei: Either[B, A]) => ei match {
        case Right(el) => Right(el)
        case Left(err) => Left(onNone(err))
      })
    )
  }

  trait StepOp[A, B] {
    def orFail (failure: B => ResultType): Step[A]
    def |? (failure: B => ResultType): Step[A] = orFail(failure)
    def |? (failureApply: => ResultType): Step[A] = orFail(_ => failureApply)
  }

  implicit def FOptionToStepA[A](futureOption: Future[Option[A]]): StepOp[A, Unit] = {
    new StepOp[A, Unit] {
      override def orFail(failure: Unit => ResultType) = ActionDSL.fromFOption(failure)(futureOption)
    }
  }

  implicit def FEitherToStepA[A, B](futureEither: Future[Either[B, A]]): StepOp[A, B] = {
    new StepOp[A, B] {
      override def orFail(failure: B => ResultType) = ActionDSL.fromFEither(failure)(futureEither)
    }
  }

  implicit def JsResultToStepA[A](futureEither: JsResult[A]): StepOp[A, JsError] = {
    new StepOp[A, JsError] {
      override def orFail(failure: JsError => ResultType) = ActionDSL.fromJsResult(failure)(futureEither)
    }
  }

  implicit def OptionToStepA[A](option: Option[A]): StepOp[A, Unit] = {
    new StepOp[A, Unit] {
      override def orFail(failure: Unit => ResultType) = ActionDSL.fromOption(failure)(option)
    }
  }
}

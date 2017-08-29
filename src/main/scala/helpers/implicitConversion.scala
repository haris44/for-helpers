package helpers

import cats.data.EitherT
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object implicitConversion {

  type Step[A] = EitherT[Future, ResultType, A]
  type ResultType = Int


  object ActionDSL {

    def fromOption[A](onNone: ResultType)(in: Future[Option[A]]): Step[A] = EitherT(
      in.map((opt: Option[A]) => opt.map(el => Right(el)).getOrElse(Left(onNone)))
    )

    def fromEither[A, B](onNone: B => ResultType)(in: Future[Either[B, A]]): Step[A] = EitherT(
      in.map((ei: Either[B, A]) => ei match {
        case Right(el) => Right(el)
        case Left(err) => Left(onNone(err))
      })
    )
  }

  trait StepOp[A, B] {
    def or(failure: B => ResultType): Step[A]
  }


  implicit def FOptionToStepA[A](futureOption: Future[Option[A]]): StepOp[A, Unit] = {
    new StepOp[A, Unit] {
      override def or(failure: Unit => ResultType) = ActionDSL.fromOption(failure())(futureOption)
    }
  }


  implicit def FEitherToStepA[A, B](futureEither: Future[Either[B, A]]): StepOp[A, B] = {
    new StepOp[A, B] {
      override def or(failure: B => ResultType) = ActionDSL.fromEither(failure)(futureEither)
    }
  }
}

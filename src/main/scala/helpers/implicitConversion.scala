package helpers

import cats.data.EitherT
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object implicitConversion {

  type Step[A] = EitherT[Future, String, A]

  object ActionDSL {

    def fromOption[A](onNone: String)(in: Future[Option[A]]): Step[A] = EitherT(
      in.map((opt: Option[A]) => opt.map(el => Right(el)).getOrElse(Left(onNone)))
    )

    def FromEither[A](onNone: String => String)(in: Future[Either[String, A]]): Step[A] = EitherT(
      in.map((ei: Either[String, A]) => ei match {
        case Right(el) => Right(el)
        case Left(err) => Left(onNone(err))
      })
    )
  }

  trait StepOp[A, B] {
    def or(failure: B => String): Step[A]
  }


  implicit def FOptionToStepA[A](futureOption: Future[Option[A]]): StepOp[A, Unit] = {
    new StepOp[A, Unit] {
      override def or(failure: Unit => String) = ActionDSL.fromOption(failure())(futureOption)
    }
  }


  implicit def FEitherToStepA[A](futureEither: Future[Either[String, A]]): StepOp[A, String] = {
    new StepOp[A, String] {
      override def or(failure: String => String ) = ActionDSL.FromEither(failure)(futureEither)
    }
  }
}

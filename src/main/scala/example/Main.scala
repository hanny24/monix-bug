package example

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp, Resource, Timer}
import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    // if replaced by Scheduler.global, it runs out of heap space eventually.
    withRuntime(Scheduler.global) { implicit runtime =>
      import runtime._
      for {
        _ <- runHttp
        _ <- Resource.liftF[Task, Unit](Task.never)
      } yield ExitCode.Success
    }
  }

  def runHttp[F[_]: ConcurrentEffect: Timer](implicit runtime: Runtime): Resource[F, Server[F]] = {
    BlazeServerBuilder[F]
      .withExecutionContext(runtime.executionContext)
      .withHttpApp(HttpApp.notFound)
      .bindHttp(8080, "127.0.0.1")
      .resource
  }

  class Runtime(implicit val concurrentEffect: ConcurrentEffect[Task], val timer: Timer[Task], val executionContext: ExecutionContext)

  def withRuntime[C, T](scheduler: Scheduler)(f: Runtime => Resource[Task, T]): IO[T] = {
    implicit val monixOptions = Task.defaultOptions.enableLocalContextPropagation
    implicit val s = scheduler
    val monixRuntime = new Runtime()
    f(monixRuntime)
      .use(Task.pure)
      .executeWithOptions(_.enableLocalContextPropagation)
      .executeOn(scheduler)
      .to[IO]
  }
}
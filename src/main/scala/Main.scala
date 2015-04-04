import akka.actor.TypedActor
import akka.pattern.ask
import config.AkkaConfigModule
import sample._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * A main class to start up the application.
 */
object Main extends App {

  implicit val config = new SystemModule with StandardCountingModule with StandardAuditModule with AkkaConfigModule {}

  config.assemble()

  // this could be called inside a supervisor actor to create a supervisor hierarchy
  val counter = config.counting

  // tell it to count three times
  counter.startCounting()
  counter.startCounting()
  counter.startCounting()

  // Create a second counter to demonstrate that `AuditCompanion` is injected under Prototype
  // scope, which means that every `CountingActor` will get its own instance of `AuditCompanion`.
  // However `AuditBus` is injected under Singleton scope. Therefore every `AuditCompanion`
  // will get a reference to the same `AuditBus`.
  val counter2 = config.counting
  counter2.startCounting()
  counter2.startCounting()

  // print the result
  for {
    actor <- Seq(counter, counter2)
    //Get a reference to the actor
    val ref = TypedActor(config.actorSystem).getActorRefFor(config.counting)

    result <- ref.ask("some message")(3.seconds).mapTo[Int]
  } {
    println(s"Got back $result from $counter")
  }

  config.actorSystem.shutdown()
  config.actorSystem.awaitTermination()
}

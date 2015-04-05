package sample

import akka.actor.{TypedActor, ActorSystem, TypedProps}
import akka.testkit.{ImplicitSender, TestKit, CallingThreadDispatcher}
import org.mockito.Mockito
import org.scalatest._

import scala.concurrent.duration._

trait Foo {
  def foo(): Unit
}

class Bar {
  def bar(): String = "bar"
}

class FooActor(bar: Bar) extends Foo {
  override def foo(): Unit = println(bar.bar())
}

class FooTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike
with OneInstancePerTest with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("FooActorTest"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  val barSpy = Mockito.spy(new Bar)
  val typedProps = TypedProps(classOf[Foo], new FooActor(barSpy)).withDispatcher(CallingThreadDispatcher.Id)

  "FooActor" should {
    "call Bar#bar() when foo is called" in {
      val fooActor: Foo = TypedActor(_system).typedActorOf(typedProps)
      fooActor.foo()
      within(200 millis) {
        Mockito.verify(barSpy).bar()
      }
    }
  }

}
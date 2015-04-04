package sample

import akka.actor.{ActorSystem, TypedActor}
import akka.testkit.{ImplicitSender, TestKit}
import config.AkkaConfigModule
import org.scalatest.{BeforeAndAfterAll, Matchers, OneInstancePerTest, WordSpecLike}

import scala.concurrent.duration._

class CountingActorSpec(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender with WordSpecLike with OneInstancePerTest
with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CountingActorSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "a Parfait-managed count actor" must {
    "send the correct count to its counting service" in {
      val testConfig =
        new SystemModule with StandardCountingModule
          with StandardAuditModule with AkkaConfigModule {
          override lazy val actorSystem: ActorSystem = _system
          override lazy val countingService = new TestCountingService()(this)
        }

      testConfig.assemble()

      val counter = testConfig.counting

      // tell it to count three times
      counter.startCounting()
      counter.startCounting()
      counter.startCounting()

      // check that it has counted correctly
      val duration = 3.seconds
      val result = counter.getCounter()
      //      Await.result(result, duration) should be(3)
      println(s"Result is $result")
      assert(result == 3)

      // check that it called the sample.TestCountingService the right number of times
      val testService = testConfig.countingService.asInstanceOf[TestCountingService]
      testService.getNumberOfCalls should be(3)
    }

    //    "send messages to its audit companion" in {
    //      val auditBusTypedProbe: TestProbe = new TestProbe(_system)
    //      val testConfig: SystemModule =
    //        new SystemModule with StandardCountingModule with StandardAuditModule with AkkaConfigModule {
    //          override lazy val actorSystem: ActorSystem = _system
    //          override lazy val auditBus = auditBusTypedProbe.ref
    //        }
    //
    //      val counter = testConfig.countingActor
    //
    //      counter ! Count
    //      auditBusTypedProbe.expectMsgClass(classOf[String])
    //
    //    }

    //@TODO: Understand how TestKit works with TypedActors
    "invoke a typed actor from a typed actor" in {
      val testConfig =
        new SystemModule with StandardCountingModule with StandardAuditModule with AkkaConfigModule {
          override lazy val actorSystem: ActorSystem = _system
        }

      testConfig.assemble()
      val counter = testConfig.counting

      counter.startCounting()
      expectNoMsg()

      //Get a reference to the actor
      val ref = TypedActor(_system).getActorRefFor(testConfig.auditBus)
      ref.tell("me message", testActor)
      expectNoMsg()
    }
  }

}

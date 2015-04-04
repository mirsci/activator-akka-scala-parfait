package sample

import akka.actor.TypedActor

trait ICounting {

  // this is not required here -- it is simply a convenience so that the name
  // can be defined and referenced from one place
  val name = "CountingActor"

  def startCounting(): Unit

  def getCounter(): Int
}

/**
 * An actor that can count using an injected CountingService.
 *
 */
class CountingImpl/*(implicit systemModule: SystemModule)*/ extends ICounting {

  val created = System.currentTimeMillis

  /**
   * DI dependencies
   */
  var auditBus : AuditBus = null
  var countingService : CountingService = null

  private var count: Int = 0

  def startCounting(): Unit = {

    println(s"[CountingActor:${TypedActor.self.hashCode()}] Message received from '$TypedActor.sender'. ")
    count = countingService.increment(count)
    auditBus.auditMessage(AuditEvent(created, s"Count is now $count"))
  }

  def getCounter(): Int = count

}

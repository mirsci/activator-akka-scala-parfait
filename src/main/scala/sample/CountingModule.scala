package sample

import akka.actor._

/**
 * A config module for the counting actor and service.
 * NOTE: This is called SampleModule in the Guice example of Akka DI.
 */

trait CountingModule {
  val counting: ICounting

  def countingService: CountingService
}

/**
 * A standard implementation configuration for CountingModule
 */
trait StandardCountingModule extends CountingModule {
  this: SystemModule =>

  lazy val counting: ICounting = newCounting(this)
  lazy val countingImpl: CountingImpl = new CountingImpl()

  def countingService: CountingService = newCountingService(this)

  /**
   * @arch factory
   */
  private def newCountingService(module: StandardCountingModule with SystemModule): CountingService = {
    new CountingService()(module)
  }

  /**
   * @arch factory
   */
  private def newCounting(module: StandardCountingModule with SystemModule): ICounting = {

    TypedActor(actorSystem).typedActorOf(TypedProps[CountingImpl](classOf[ICounting], countingImpl))
  }

  def assemble(): Unit = {
    /**
     * Inject dependencies
     */
    countingImpl.countingService = this.countingService
    countingImpl.auditBus = this.auditBus
  }

}

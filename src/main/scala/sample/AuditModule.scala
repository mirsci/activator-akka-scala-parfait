package sample

import akka.actor.{TypedActor, TypedProps}
import config.ConfigModule

trait AuditModule {
  def auditBus: AuditBus
}

trait StandardAuditModule extends AuditModule {
  this: ConfigModule =>

  lazy val auditBus: AuditBus = TypedActor(actorSystem).typedActorOf(TypedProps[AuditBusImpl]())

}

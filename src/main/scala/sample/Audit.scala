package sample

import akka.actor.TypedActor

case class AuditEvent(auditCompanionCreated: Long, msg: Any)

trait AuditBus {
  val name = "AuditBusImpl"

  def auditMessage(event: AuditEvent) : Unit
}

class AuditBusImpl extends AuditBus {

  def auditMessage(event: AuditEvent) : Unit = {
    val msg = event.msg
    val companionCreated = event.auditCompanionCreated
    println("###Message " + msg + ", " + msg.getClass.getName)
    println(s"[AuditBus:${TypedActor.self.hashCode()}] Message '$msg' received from '$TypedActor.sender'. " +
      s"AuditCompanion created at '$companionCreated'.")
  }


}




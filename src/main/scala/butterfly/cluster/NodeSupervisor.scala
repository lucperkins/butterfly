package butterfly.cluster

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy}

import scala.concurrent.duration._

class NodeSupervisor(maxRetries: Int = 10, withinTimeRange: Duration = 1 minute) extends Actor {
  override val supervisorStrategy = OneForOneStrategy(
    maxNrOfRetries = maxRetries,
    withinTimeRange = withinTimeRange
  ) {
      case _: ArithmeticException  =>  Resume
      case _: NullPointerException => Restart
      case _: Exception            => Escalate
    }

  def receive: Receive = {
    case _ =>
  }
}
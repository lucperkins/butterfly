package butterfly.cluster

import akka.actor.ActorSystem
import butterfly.RiakWorker
import butterfly.requests.{KVRequests}

case class RiakNode(host: String, port: Int)(implicit val system: ActorSystem)
  extends KVRequests {

  val worker = RiakWorker(host, port)
  def disconnect() = system stop worker.actor
}

object RiakNode {
  sealed trait State

  object State {
    case object RUNNING extends State

  }

  def apply(host: String, port: Int, connection: Int = 12)(implicit system: ActorSystem): RiakNode = {
    val client = RiakNode(host, port)
    client
  }
}
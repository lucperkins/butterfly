package butterfly.cluster

import akka.actor.ActorSystem
import butterfly.RiakWorker
import butterfly.requests.{KVRequests, SearchRequests}

case class RiakNode(host: String, port: Int)(implicit val system: ActorSystem)
  extends KVRequests with SearchRequests {
  val worker = RiakWorker(host, port)
  def disconnect() = system stop worker.actor
}

object RiakNode {
  def apply(host: String, port: Int, connection: Int = 12)(implicit system: ActorSystem): RiakNode = {
    val client = RiakNode(host, port)
    client
  }
}
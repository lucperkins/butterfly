package butterfly.cluster

import akka.actor.ActorSystem
import butterfly.RiakClient

case class RiakNode(host: String, port: Int)(implicit val system: ActorSystem) {
  val client = RiakClient(host, port)
}
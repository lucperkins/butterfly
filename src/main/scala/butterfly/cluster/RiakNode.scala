package butterfly.cluster

import akka.actor.{Actor, ActorSystem, Props}
import butterfly.core.RiakWorker
import butterfly.requests.KVRequests

case class RiakClient(host: String, port: Int)
                     (implicit val system: ActorSystem) extends KVRequests {

  val worker = RiakWorker(host, port)
  def disconnect() = system stop worker.actor
}

object NodeMessages {
  case object Start
  case object Stop
}

object RiakNode {
  implicit val system = ActorSystem("node-system")

  def props(host: String, port: Int): Props =
    Props(new RiakNode(host, port))
}

class RiakNode(host: String, port: Int)
              (implicit system: ActorSystem) extends Actor {
  import butterfly.cluster.NodeMessages._

  val client = new RiakClient(host, port)

  def preRestart(): Unit = {

  }

  def receive: Receive = {
    case Start =>

    case Stop =>
      client.disconnect()
  }
}
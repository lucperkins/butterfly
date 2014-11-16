package butterfly

import akka.actor.Actor

object Sender {
  case class Get(bucket: String,
                 key: String,
                 bucketType: String = "default",
                 props: FetchProperties)
}

class Sender extends Actor {
  import Sender._

  def receive: Receive = {
    case _ => println("Received!")
  }
}
package butterfly

import java.net.InetSocketAddress

import akka.actor.ActorSystem

object Main extends App {
  val system = ActorSystem("riak-system")
  val endpoint = new InetSocketAddress("localhost", 10017)
  system.actorOf(RiakClient.props(endpoint), "riak-connection")

}
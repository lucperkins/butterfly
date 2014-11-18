package butterfly

import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem, Actor}
import akka.io.Tcp.{Write, CommandFailed, Bind}
import akka.io.{Tcp, IO}
import akka.util.ByteString
import com.basho.riak.protobuf.RiakKvPB.RpbGetReq
import nl.gideondk.sentinel.Client

import scala.concurrent.duration.Duration

object Main extends App {
  val address = new InetSocketAddress("127.0.0.1", 10017)
  val system = ActorSystem("riak-system")
  system.actorOf(RiakConnection.props(address), "riak-actor")
}
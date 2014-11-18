package butterfly

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Actor, ActorRef, Props}
import akka.io.Tcp.{Connect, CommandFailed}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.google.protobuf.Message
import com.google.protobuf.Message.Builder

class RiakListener extends Actor {
  case object DoSomething

  def receive: Receive = {
    case DoSomething =>
      println("EVERYTHINS IS AWESOME")
  }
}

object RiakClient {
  def props(remote: InetSocketAddress) = {
    Props(new RiakClient(remote))
  }
}

class RiakClient(remote: InetSocketAddress) extends Actor {
  case object DoSomething
  import context.system

  IO(Tcp) ! Connect(remote)

  val listener = context.actorOf(Props[RiakListener])

  def receive: Receive = {
    case c @ Tcp.Connected(remote, _) =>
      listener ! DoSomething
  }
}
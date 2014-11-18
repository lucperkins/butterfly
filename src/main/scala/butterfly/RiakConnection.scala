package butterfly

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props, Actor}
import akka.io.{IO, Tcp}
import akka.util.ByteString

object RiakConnection {
  def props(address: InetSocketAddress): Props =
    Props(new RiakConnection(address, None))
}

private case class Connect(address: InetSocketAddress)
private case class CommandAck(sender: ActorRef) extends Tcp.Event
private case class Send(message: ByteString)

object Messages {
  val message =
    ByteString.fromArray(MessageBuilder.getRequest("test", "test", "default", new FetchProperties()).toByteArray)
}

class RiakConnection(address: InetSocketAddress,
                     maxConnectionAttempts: Option[Int]) extends Actor {

  var socket: ActorRef = _
  self ! Connect(address)
  def receive: Receive = {
    case Send(message) => println(message.utf8String)
    case Connect(a) =>
      println("CONNECTING")
      IO(Tcp)(context.system) ! Tcp.Connect(address)
    case conn: Tcp.Connected =>
      println("CONNECTED")
      //sender ! Tcp.Register(self, useResumeWriting = false)
      println("WRITING MESSAGE")
      sender ! Tcp.Write(Messages.message, CommandAck(sender()))
    case Tcp.CommandFailed => println("FAILED")
    case Tcp.Received(data) => println("SUCCESS")
    case _ => println("boo")
  }
}

object RiakNode {
  def apply(host: String, port: Int): Props = Props(classOf[RiakNode], host, port)
  def apply(): Props = apply("127.0.0.1", 8087)
}

class RiakNode(host: String, port: Int) extends Actor {
  val address = new InetSocketAddress(host, port)
  val connection = context.actorOf(Props(classOf[RiakConnection], self, address))

  def receive: Receive = {
    case conn: Tcp.Connected =>
      connection ! Send(Messages.message)
  }
}
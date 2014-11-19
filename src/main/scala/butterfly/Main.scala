//package butterfly

import java.net.InetSocketAddress

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.stream.io.StreamTcp
import akka.stream.scaladsl.{Sink, Source}
import akka.util.{ByteString, Timeout}
import akka.stream._

import concurrent.duration._
import scala.util.{Failure, Success}

object Client {
  def client(system: ActorSystem, address: InetSocketAddress): Unit = {
    implicit val sys = system
    import system.dispatcher
    implicit val materializer = FlowMaterializer()
    implicit val timeout = Timeout(5.seconds)
    val clientFuture = IO(StreamTcp) ? StreamTcp.Connect(address)
    clientFuture.onSuccess {
      case clientBinding: StreamTcp.OutgoingTcpConnection =>
        val testInput = ('a' to 'z').map(ByteString(_))
        Source(testInput).to(Sink(clientBinding.outputStream)).run()
        Source(clientBinding.inputStream).fold(Vector.empty[Char]) { (acc, in) => acc ++ in.map(_.asInstanceOf[Char])}.
          onComplete {
            case Success(result) =>
              println(s"Result: " + result.mkString("[", ", ", "]"))
              println("Shutting down client")
              system.shutdown()
            case Failure(e) =>
              println("Failure: " + e.getMessage)
              system.shutdown()
        }
    }

    clientFuture.onFailure {
      case e: Throwable =>
        println(s"Client could not connect to $address: ${e.getMessage}")
        system.shutdown()
    }
  }
}

object Main extends App {
  val system = ActorSystem("main-actor-system")
  val address = new InetSocketAddress("127.0.0.1", 10017)
  Client.client(system, address)
}
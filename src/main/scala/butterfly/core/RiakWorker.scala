package butterfly.core

import akka.actor.ActorSystem
import com.google.protobuf.{ByteString => BS}
import nl.gideondk.sentinel.Client

import scala.concurrent.duration._

class RiakWorker

object RiakWorker extends RiakStages {
  def apply(host: String, port: Int, numberOfWorkers: Int = 4)(implicit system: ActorSystem) = {
    Client.randomRouting(
      host,
      port,
      numberOfWorkers,
      "Riak",
      stages,
      5 seconds,
      RiakMessageHandler,
      true, // allow pipelining
      1024 * 8, // lowBytes
      1024 * 1024 * 5, // highBytes
      1024 * 1024 * 200 //maxBufferSize
    )(system)
  }
}
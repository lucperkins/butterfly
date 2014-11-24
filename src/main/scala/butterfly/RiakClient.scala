package butterfly

import akka.actor.ActorSystem
import butterfly.requests.{KVRequests, SearchRequests}
import com.basho.riak.protobuf.RiakKvPB.{RpbGetResp, RpbGetReq}

case class RiakClient(host: String, port: Int)(implicit val system: ActorSystem)
  extends KVRequests with SearchRequests {
  val worker = RiakWorker(host, port)
  def disconnect() = system stop worker.actor
}

object RiakClient {
  def apply(host: String, port: Int, connection: Int = 12)(implicit system: ActorSystem): RiakClient = {
    val client = RiakClient(host, port)
    client
  }
}
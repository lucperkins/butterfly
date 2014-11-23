package butterfly

import akka.actor.ActorSystem
import com.basho.riak.protobuf.RiakKvPB.{RpbGetResp, RpbGetReq}
import com.google.protobuf.ByteString

import scala.concurrent.Future

case class RiakClient(host: String, port: Int)(implicit val system: ActorSystem)
  extends RiakRequest with ByteStringConverter {
  val worker = RiakWorker(host, port)

  def get(bucket: String, key: String): Future[RpbGetResp] = {
    println("Running fetch request")
    val msg = RpbGetReq.newBuilder()
      .setBucket(ByteString.copyFromUtf8(bucket))
      .setKey(ByteString.copyFromUtf8(key))
      .build()

    val req = buildRequest(RiakMessageType.RpbGetReq, protobufToAkka(msg.toByteString))
    req.map(resp => {
      println(resp)
      val rawResponse = akkaToProtobuf(resp.message)
      RpbGetResp.parseFrom(rawResponse)
    })
  }

  def disconnect() = system stop worker.actor
}

object RiakClient {
  def apply(host: String, port: Int, connection: Int = 12)(implicit system: ActorSystem): RiakClient = {
    val client = RiakClient(host, port)
    client
  }
}
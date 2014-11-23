package butterfly

import akka.actor.ActorSystem
import akka.util.ByteString
import com.basho.riak.protobuf.RiakPB.RpbErrorResp
import nl.gideondk.sentinel.Client

import scala.concurrent.Future
import scala.util.{Success, Failure, Try}

trait RiakRequest extends ByteStringConverter {
  def system: ActorSystem
  def worker: Client[RiakMessage, RiakMessage]
  implicit val dispatcher = system.dispatcher

  def buildRequest(messageType: RiakMessageType, message: ByteString): Future[RiakMessage] = {
    val msg = RiakMessage(messageType, akkaToProtobuf(message))
    (worker ? msg).flatMap(resp => validateResponse(resp) match {
      case Failure(err) => Future.failed(err)
      case Success(s)   => Future(s)
    })
  }

  def buildRequest(messageType: RiakMessageType): Future[RiakMessage] = buildRequest(messageType, ByteString())

  def validateResponse(resp: RiakMessage): Try[RiakMessage] = {
    if (RiakMessageType.messageTypeToInt(resp.messageType) == 0) {
      val pbcMessage = RpbErrorResp.parseFrom(resp.message).getErrmsg.toStringUtf8
      Failure(new Exception(pbcMessage))
    } else {
      Success(resp)
    }
  }
}

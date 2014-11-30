package butterfly.core

import com.basho.riak.protobuf.RiakPB.RpbErrorResp
import nl.gideondk.sentinel.Client

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class RiakOperation(implicit val worker: Client[RiakMessage, RiakMessage], implicit val ec: ExecutionContext) {
  def execute(message: RiakMessage): Future[RiakMessage] = {
    val msg = RiakMessage(message.messageType, message.message)
    (worker ? msg).flatMap(resp => validateResponse(resp) match {
      case Failure(err) => Future.failed(err)
      case Success(m)   => Future(m)
    })
  }

  private def validateResponse(response: RiakMessage): Try[RiakMessage] = {
    if (RiakMessageType.messageTypeToInt(response.messageType) == 0) {
      val pbcErrorMessage = RpbErrorResp.parseFrom(response.message).getErrmsg.toStringUtf8
      Failure(new Exception(pbcErrorMessage))
    } else {
      Success(response)
    }
  }
}
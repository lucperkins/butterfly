package butterfly

import akka.actor.ActorSystem
import com.basho.riak.protobuf.RiakPB.RpbErrorResp
import com.basho.riak.protobuf._
import nl.gideondk.sentinel.Client
import butterfly.RiakMessageType

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

class RiakOperation(implicit val worker: Client[RiakMessage, RiakMessage], system: ActorSystem) {
  def execute[S <: RiakMessage, R <: RiakMessage](message: S): Future[R] = {
    val msg = RiakMessage(message.messageType, message.message)
    (worker ? msg).flatMap(resp => validateResponse(resp) match {
      case Failure(err) => Future.failed(err)
      case Success(message) => Future(message)
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

class Get extends RiakOperation {

}
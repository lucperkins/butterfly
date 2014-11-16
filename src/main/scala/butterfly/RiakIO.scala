package butterfly

import akka.io.IO
import com.basho.riak.protobuf.RiakKvPB._
import com.google.protobuf.{Message, ByteString}

sealed class RiakInteraction[S <: Message, R <: Message] {
  def sendReceive(s: S): Option[R] = None
}

object RiakIO {
  def get(getReq: RpbGetReq): Option[RpbGetResp] = {
    val com = new RiakInteraction[RpbGetReq, RpbGetResp]
    val response = com.sendReceive(getReq)
    response
  }

  def put(putReq: RpbPutReq): Option[RpbPutResp] = {
    val com = new RiakInteraction[RpbPutReq, RpbPutResp]
    val response = com.sendReceive(putReq)
    response
  }

  /*
  def delete(deleteReq: RpbDelReq): Option[Boolean] = {
    val com = new RiakInteraction[RpbDelReq, Boolean]
    val response = com.sendReceive(deleteReq)
    response
  }
  */
}